package ru.nucodelabs.algorithms.interpolation;

import javafx.scene.canvas.Canvas;
import javafx.scene.chart.XYChart;
import javafx.scene.image.PixelWriter;
import org.apache.commons.math3.analysis.interpolation.BicubicInterpolatingFunction;
import org.apache.commons.math3.analysis.interpolation.BicubicInterpolator;
import ru.nucodelabs.files.color_palette.CLRData;
import ru.nucodelabs.files.color_palette.CLRFileParser;
import ru.nucodelabs.gem.view.color_palette.ColorPalette;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PseudoInterpolator {

    //Расстояние от пикета до нуля
    private final List<Double> xs = new ArrayList<>();
    //Список всех ab_2 пикетов
    //private final List<List<Double>> abs = new ArrayList<>();
    //Список всех сопротивлений пикетов
    //private final List<List<Double>> resistances = new ArrayList<>();

    private Double minRo = 0d;
    private Double maxRo = 0d;

    //Линии по общим для всех пикетов ab_2
    private final List<Line> lines = new ArrayList<>();

    public PseudoInterpolator(List<List<XYChart.Data<Double, Double>>> listList) {
        //Минимальное количество измерений (линий) у пикета
        int minLinesCnt = listList.stream().map(List::size).mapToInt(Integer::intValue).min().orElse(0);

        //Инициализация линий
        for (int i = 0; i < minLinesCnt; i++) {
            lines.add(new Line(listList.get(0).get(i).getYValue()));
        }

        minRo = (Double) listList.get(0).get(0).getExtraValue();

        //Разбор пикетов
        for (int i = 0; i < listList.size(); i++) {
            List<XYChart.Data<Double, Double>> currentList = listList.get(i);
            xs.add(currentList.get(0).getXValue());
//            abs.add(new ArrayList<>());
//            resistances.add(new ArrayList<>());

            double currentPicketLinesCnt = 0;

            //Разбор пикета
//            for (XYChart.Data<Double, Double> data : currentList) {
//                abs.get(i).add(data.getYValue());
//                resistances.get(i).add((Double) data.getExtraValue());
//            }
            for (int j = 0; j < minLinesCnt; j++) {
                double currentResistance = (Double) currentList.get(j).getExtraValue();
                lines.get(j).addResistance(currentResistance);
                minRo = Math.min(minRo, currentResistance);
                maxRo = Math.max(maxRo, currentResistance);
            }
        }
    }

    public BicubicInterpolatingFunction getInterpolationFunction() {
        double[] xval = xs.stream().mapToDouble(Double::doubleValue).toArray();
        double[] yval = lines.stream().map(Line::getAb_2).mapToDouble(Double::doubleValue).toArray();
        double[][] fval = new double[xs.size()][lines.size()];

        for (int i = 0; i < xs.size(); i++) {
            for (int j = 0; j < lines.size(); j++) {
                fval[i][j] = lines.get(j).getResistances().get(i);
            }
        }

        BicubicInterpolator interpolator = new BicubicInterpolator();

        return interpolator.interpolate(xval, yval, fval);
    }

    public void paint(Canvas canvas) throws Exception {
        PixelWriter pw = canvas.getGraphicsContext2D().getPixelWriter();

        CLRFileParser fileParser = new CLRFileParser(new File(
                "data/clr/002_ERT_Rainbow_2.clr")
        );
        CLRData clrData = fileParser.parse();
        ColorPalette palette = new ColorPalette(clrData);
        palette.setMinValue(minRo);
        palette.setMaxValue(maxRo);

        double rangeY = lines.get(lines.size() - 1).getAb_2() - lines.get(0).getAb_2();
        double stepY = rangeY / canvas.getHeight();
        double rangeX = xs.get(xs.size() - 1) - xs.get(0);
        double stepX = rangeX / canvas.getWidth();
        double curY = lines.get(0).getAb_2();

        BicubicInterpolatingFunction function = this.getInterpolationFunction();
        for (int i = 0; i < canvas.getHeight(); i++) {
            //Первый пикет на расстоянии 0
            double curX = xs.get(0); // = 0
            for (int j = 0; j < canvas.getWidth(); j++) {
                pw.setColor(j, i, palette.colorForValue(function.value(curX, curY)));
                curX += stepX;
            }
            curY += stepY;
        }
    }

}

//Линия по общему для всех пикетов ab_2 с сопротивлениями всех пикетов
class Line implements Comparable<Line> {

    private final Double ab_2;
    private final List<Double> resistances = new ArrayList<>();

    Line(Double ab_2) {
        this.ab_2 = ab_2;
    }

    void addResistance(Double resistance) {
        this.resistances.add(resistance);
    }

    public Double getAb_2() {
        return ab_2;
    }

    public List<Double> getResistances() {
        return resistances;
    }

    @Override
    public int compareTo(Line line1) {
        if (this.getAb_2() - line1.getAb_2() < 0) {
            return -1;
        } else if (this.getAb_2() - line1.getAb_2() == 0) {
            return 0;
        } else {
            return 1;
        }
    }
}