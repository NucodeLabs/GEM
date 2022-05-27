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
import java.util.stream.Collectors;

public class PseudoCrossInterpolator {

    //Разрез
    private final Section section;
    //Список пикетов
    private final List<Picket> pickets;
    //Расстояние от пикета до нуля
    private final List<Double> Xs = new ArrayList<>();

    //Линии по общим для всех пикетов ab_2
    private final List<Line> lines = new ArrayList<>();

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
                Line line = lines.stream().filter(l -> Objects.equals(l.getAb_2(), currentAb_2)).findFirst().orElse(null);
                if (line == null) {
                    Line newLine = new Line(currentAb_2);
                    newLine.addResistance(expData.getResistanceApparent());
                    lines.add(newLine);
                } else {
                    for (Line l : lines) {
                        if (Objects.equals(l, line)) {
                            l.addResistance(expData.getResistanceApparent());
                        }
                    }
                }
                minRo = Math.min(minRo, expData.getResistanceApparent());
                maxRo = Math.max(maxRo, expData.getResistanceApparent());
            }

        }

        //Оставляет только те ab_2, которые есть во всех пикетах
        List<Line> newLines = lines.stream().filter(line -> line.getResistances().size() == pickets.size()).toList();
        lines.clear();
        lines.addAll(newLines);
        Collections.sort(lines);
    }


    public BicubicInterpolatingFunction getInterpolationFunction() {
        double[] xval = Xs.stream().mapToDouble(Double::doubleValue).toArray();
        double[] yval = lines.stream().map(Line::getAb_2).mapToDouble(Double::doubleValue).toArray();
        double[][] fval = new double[Xs.size()][lines.size()];

        for (int i = 0; i < lines.size(); i++) {
            for (int j = 0; j < pickets.size(); j++) {
                fval[i][j] = lines.get(i).getResistances().get(j);
            }
        }

        BicubicInterpolator interpolator = new BicubicInterpolator();

        return interpolator.interpolate(xval, yval, fval);
    }

    public Canvas paint(Canvas canvas) throws Exception {
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
        double rangeX = Xs.get(Xs.size() - 1) - Xs.get(0);
        double stepX = rangeX / canvas.getWidth();
        double curY = 0;

        BicubicInterpolatingFunction function = this.getInterpolationFunction();
        for (int i = 0; i < canvas.getHeight(); i++) {
            //Первый пикет на расстоянии 0
            double curX = Xs.get(0); // = 0
            for (int j = 0; j < canvas.getWidth(); j++) {
                pw.setColor(j, i, palette.colorForValue(function.value(curX, curY)));
            }
            curY += stepY;
        }

        return canvas;
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
    public int compareTo(Line line) {
        if (this.getAb_2() - line.getAb_2() < 0) {
            return -1;
        } else if (this.getAb_2() - line.getAb_2() == 0) {
            return 0;
        } else {
            return 1;
        }
    }
}