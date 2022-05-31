package ru.nucodelabs.algorithms.interpolation;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelWriter;
import org.apache.commons.math3.analysis.interpolation.BicubicInterpolatingFunction;
import org.apache.commons.math3.analysis.interpolation.BicubicInterpolator;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.files.color_palette.CLRData;
import ru.nucodelabs.files.color_palette.CLRFileParser;
import ru.nucodelabs.gem.view.color_palette.ColorPalette;

import java.io.File;
import java.util.*;

public class PseudoCrossInterpolator {

    //Разрез
    private final Section section;
    //Список пикетов
    private final List<Picket> pickets;
    //Расстояние от пикета до нуля
    private final List<Double> Xs = new ArrayList<>();

    //Линии по общим для всех пикетов ab_2
    private final List<Line1> line1s = new ArrayList<>();

    private Double minRo = 0.0;
    private Double maxRo = 0.0;

    PseudoCrossInterpolator(Section section) {
        this.section = section;
        pickets = section.getPickets();
        for (Picket picket : pickets) {
            List<ExperimentalData> experimentalData = picket.getExperimentalData();

            Xs.add(picket.getOffsetX());

            for (ExperimentalData expData : experimentalData) {
                Double currentAb_2 = expData.getAb2();
                Line1 line1 = line1s.stream().filter(l -> Objects.equals(l.getAb_2(), currentAb_2)).findFirst().orElse(null);
                if (line1 == null) {
                    Line1 newLine1 = new Line1(currentAb_2);
                    newLine1.addResistance(expData.getResistanceApparent());
                    line1s.add(newLine1);
                } else {
                    for (Line1 l : line1s) {
                        if (Objects.equals(l, line1)) {
                            l.addResistance(expData.getResistanceApparent());
                        }
                    }
                }
                minRo = Math.min(minRo, expData.getResistanceApparent());
                maxRo = Math.max(maxRo, expData.getResistanceApparent());
            }

        }

        //Оставляет только те ab_2, которые есть во всех пикетах
        List<Line1> newLine1s = line1s.stream().filter(line1 -> line1.getResistances().size() == pickets.size()).toList();
        line1s.clear();
        line1s.addAll(newLine1s);
        Collections.sort(line1s);
    }


    public BicubicInterpolatingFunction getInterpolationFunction() {
        double[] xval = Xs.stream().mapToDouble(Double::doubleValue).toArray();
        double[] yval = line1s.stream().map(Line1::getAb_2).mapToDouble(Double::doubleValue).toArray();
        double[][] fval = new double[Xs.size()][line1s.size()];

        for (int i = 0; i < line1s.size(); i++) {
            for (int j = 0; j < pickets.size(); j++) {
                fval[i][j] = line1s.get(i).getResistances().get(j);
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

        double rangeY = line1s.get(line1s.size() - 1).getAb_2() - line1s.get(0).getAb_2();
        double stepY = rangeY / canvas.getHeight();
        double rangeX = Xs.get(Xs.size() - 1) - Xs.get(0);
        double stepX = rangeX / canvas.getWidth();
        double curY = 0;

        BicubicInterpolatingFunction function = this.getInterpolationFunction();
        for (int i = 0; i < canvas.getHeight(); i++) {
            //Первый пикет на расстоянии 0
            double curX = Xs.get(0); // = 0
            for (int j = 0; j < canvas.getWidth(); j++) {
                pw.setColor(j, i, palette.colorForValue(function.value(curX, curY)));
                curX += stepX;
            }
            curY += stepY;
        }
    }

}

//Линия по общему для всех пикетов ab_2 с сопротивлениями всех пикетов
class Line1 implements Comparable<Line1> {

    private final Double ab_2;
    private final List<Double> resistances = new ArrayList<>();

    Line1(Double ab_2) {
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
    public int compareTo(Line1 line1) {
        if (this.getAb_2() - line1.getAb_2() < 0) {
            return -1;
        } else if (this.getAb_2() - line1.getAb_2() == 0) {
            return 0;
        } else {
            return 1;
        }
    }
}