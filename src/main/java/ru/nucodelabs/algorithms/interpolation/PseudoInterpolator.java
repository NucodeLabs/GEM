package ru.nucodelabs.algorithms.interpolation;

import javafx.scene.canvas.Canvas;
import javafx.scene.chart.XYChart;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.BicubicInterpolatingFunction;
import org.apache.commons.math3.analysis.interpolation.BicubicInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import ru.nucodelabs.gem.view.color.ColorMapper;
import smile.interpolation.RBFInterpolation;
import smile.interpolation.RBFInterpolation2D;
import smile.math.rbf.GaussianRadialBasis;
import smile.math.rbf.InverseMultiquadricRadialBasis;
import smile.math.rbf.MultiquadricRadialBasis;
import smile.math.rbf.RadialBasisFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PseudoInterpolator {

    //Расстояние от пикета до нуля
    private final List<Double> xs = new ArrayList<>();
    private final ColorMapper colorPalette;

    //Линии по общим для всех пикетов ab_2
    private final List<Line> lines = new ArrayList<>();

    //Входные данные
    private final List<List<XYChart.Data<Double, Double>>> inputPoints;

    private RBFInterpolation2D rbfInterpolation2D;

    public PseudoInterpolator(List<List<XYChart.Data<Double, Double>>> listList, ColorMapper colorPalette) {
        this.inputPoints = listList;
        this.colorPalette = colorPalette;

        InterpolationDataParser interpolationDataParser = new InterpolationParser(inputPoints);
        interpolationDataParser.parse();

        //Минимальное количество измерений (линий) у пикета
        int minLinesCnt = listList.stream().map(List::size).mapToInt(Integer::intValue).min().orElse(0);

        //Инициализация линий
        for (int i = 0; i < minLinesCnt; i++) {
            lines.add(new Line(listList.get(0).get(i).getYValue()));
        }

        //Разбор пикетов
        for (List<XYChart.Data<Double, Double>> currentList : listList) {
            xs.add(currentList.get(0).getXValue());

            for (int j = 0; j < minLinesCnt; j++) {
                double currentResistance = (Double) currentList.get(j).getExtraValue();
                lines.get(j).addResistance(currentResistance);
            }
        }

        //Удаление одинаковых линий
        for (int i = 1; i < lines.size(); i++) {
            if (Objects.equals(lines.get(i).getAb_2(), lines.get(i - 1).getAb_2())) {
                lines.remove(i);
                i--;
            }
        }
    }

    public BivariateFunction getInterpolationFunction() {
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

    public UnivariateFunction getSplineInterpolateFunction() {
        double[] xval = lines.stream().map(Line::getAb_2).mapToDouble(Double::doubleValue).toArray();
        double[] yval = new double[lines.size()];

        for (int i = 0; i < lines.size(); i++) {
            yval[i] = lines.get(i).getResistances().get(0);
        }

        SplineInterpolator interpolator = new SplineInterpolator();

        return interpolator.interpolate(xval, yval);
    }

    public void rbfInterpolateInit() {

        int size = xs.size() * lines.size();
        double[] x1 = new double[size]; //xs
        double[] x2 = new double[size]; //lines
        double[] y = new double[size]; //rhos

        for (int i = 0; i < xs.size(); i++) {
            for (int j = 0; j < lines.size(); j++) {
                x1[i * lines.size() + j] = xs.get(i);
                x2[i * lines.size() + j] = inputPoints.get(i).get(j).getYValue();
                y[i * lines.size() + j] = (Double) inputPoints.get(i).get(j).getExtraValue();
            }
        }

        RadialBasisFunction radialBasisFunction = new MultiquadricRadialBasis();
        this.rbfInterpolation2D = new RBFInterpolation2D(x1, x2, y, new smile.math.rbf.GaussianRadialBasis());
    }

    public void paint(Canvas canvas) {
        if (xs.size() == 1) {
            UnivariateFunction function = getSplineInterpolateFunction();
            PixelWriter pw = canvas.getGraphicsContext2D().getPixelWriter();

            double stepY = (double) lines.size() / Math.ceil(canvas.getHeight());
            double curY = 0;
            double curArg = lines.get(0).getAb_2();

            for (int i = 0; i < canvas.getHeight(); i++) {
                Color color = colorPalette.colorFor(function.value(curArg));
                curY += stepY;
                int rounding = (int) Math.floor(curY);
                if (rounding >= lines.size()) {
                    rounding = lines.size() - 1;
                }
                curArg = lines.get(rounding).getAb_2();
                for (int j = 0; j < canvas.getWidth(); j++) {
                    pw.setColor(j, i, color);
                }
            }
            return;
        }
        if (xs.size() < 1) {
            var gc = canvas.getGraphicsContext2D();
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            return;
        }

        PixelWriter pw = canvas.getGraphicsContext2D().getPixelWriter();

        double rangeY = lines.get(lines.size() - 1).getAb_2() - lines.get(0).getAb_2();
        double stepY = rangeY / canvas.getHeight();
        double rangeX = xs.get(xs.size() - 1) - xs.get(0);
        double stepX = rangeX / canvas.getWidth();
        double curY = lines.get(0).getAb_2();

        BivariateFunction function = this.getInterpolationFunction();
        rbfInterpolateInit();
        for (int i = 0; i < canvas.getHeight(); i++) {
            //Первый пикет на расстоянии 0
            double curX = xs.get(0); // = 0
            for (int j = 0; j < canvas.getWidth(); j++) {
                //pw.setColor(j, i, colorPalette.colorFor(function.value(curX, curY)));
                pw.setColor(j, i, colorPalette.colorFor(this.rbfInterpolation2D.interpolate(curX, curY)));
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