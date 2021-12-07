package ru.nucodelabs.gem;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import ru.nucodelabs.data.ExperimentalData;

import java.util.List;

import static java.lang.Math.log10;
import static java.lang.Math.max;

public class ExperimentalCurve {

    protected static void makeCurve(
            LineChart<Double, Double> vesCurve, ExperimentalData experimentalData) {
        vesCurve.getData().add(makeCurveData(experimentalData.getAB_2(), experimentalData.getResistanceApparent()));
        vesCurve.getData().add(makeCurveErrorUpper(experimentalData.getAB_2(), experimentalData.getResistanceApparent(), experimentalData.getErrorResistanceApparent()));
        vesCurve.getData().add(makeCurveErrorLower(experimentalData.getAB_2(), experimentalData.getResistanceApparent(), experimentalData.getErrorResistanceApparent()));
    }

    private static XYChart.Series<Double, Double> makeCurveData(List<Double> AB_2, List<Double> resistanceApparent) {
        XYChart.Series<Double, Double> pointsSeries = new XYChart.Series<>();

        for (int i = 0; i < AB_2.size(); i++) {
            double dotX = log10(AB_2.get(i));
            double dotY = max(log10(resistanceApparent.get(i)), 0);
            pointsSeries.getData().add(new XYChart.Data<>(dotX, dotY));
        }
        return pointsSeries;
    }

    protected static XYChart.Series<Double, Double> makeCurveErrorUpper(List<Double> AB_2,
                                                                        List<Double> resistanceApparent,
                                                                        List<Double> errorResistanceApparent) {
        XYChart.Series<Double, Double> pointsSeries = new XYChart.Series<>();

        for (int i = 0; i < AB_2.size(); i++) {
            double dotX = log10(AB_2.get(i));
            double error = errorResistanceApparent.get(i) / 100f;
            double dotY = max(log10(resistanceApparent.get(i) + resistanceApparent.get(i) * error), 0);
            pointsSeries.getData().add(new XYChart.Data<>(dotX, dotY));
        }
        return pointsSeries;
    }

    protected static XYChart.Series<Double, Double> makeCurveErrorLower(List<Double> AB_2,
                                                                        List<Double> resistanceApparent,
                                                                        List<Double> errorResistanceApparent) {
        XYChart.Series<Double, Double> pointsSeries = new XYChart.Series<>();

        for (int i = 0; i < AB_2.size(); i++) {
            double dotX = log10(AB_2.get(i));
            double error = errorResistanceApparent.get(i) / 100f;
            double dotY = max(log10(resistanceApparent.get(i) - resistanceApparent.get(i) * error), 0);
            pointsSeries.getData().add(new XYChart.Data<>(dotX, dotY));
        }
        return pointsSeries;
    }
}
