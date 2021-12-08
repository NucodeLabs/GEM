package ru.nucodelabs.gem.charts;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import ru.nucodelabs.data.ExperimentalData;

import java.util.ArrayList;

import static java.lang.Math.log10;
import static java.lang.Math.max;

public class ExperimentalCurve {

    protected static void addData(LineChart<Double, Double> vesCurve, ExperimentalData experimentalData) {
        vesCurve.getData().add(makeCurveData(experimentalData));
        vesCurve.getData().add(makeCurveErrorUpper(experimentalData));
        vesCurve.getData().add(makeCurveErrorLower(experimentalData));
    }

    protected static void initializeWithData(LineChart<Double, Double> vesCurve, ExperimentalData experimentalData) {
        vesCurve.getData().clear();
        vesCurve.getXAxis().setAutoRanging(true);
        addData(vesCurve, experimentalData);
        vesCurve.setVisible(true);
    }

    private static XYChart.Series<Double, Double> makeCurveData(ExperimentalData experimentalData) {
        final ArrayList<Double> AB_2 = new ArrayList<>(experimentalData.getAB_2());
        final ArrayList<Double> resistanceApparent = new ArrayList<>(experimentalData.getResistanceApparent());

        XYChart.Series<Double, Double> pointsSeries = new XYChart.Series<>();
        for (int i = 0; i < experimentalData.getSize(); i++) {
            double dotX = log10(AB_2.get(i));
            double dotY = max(log10(resistanceApparent.get(i)), 0);
            pointsSeries.getData().add(new XYChart.Data<>(dotX, dotY));
        }
        return pointsSeries;
    }

    protected static XYChart.Series<Double, Double> makeCurveErrorUpper(ExperimentalData experimentalData) {
        final ArrayList<Double> AB_2 = new ArrayList<>(experimentalData.getAB_2());
        final ArrayList<Double> resistanceApparent = new ArrayList<>(experimentalData.getResistanceApparent());
        final ArrayList<Double> errorResistanceApparent = new ArrayList<>(experimentalData.getErrorResistanceApparent());

        XYChart.Series<Double, Double> pointsSeries = new XYChart.Series<>();
        for (int i = 0; i < experimentalData.getSize(); i++) {
            double dotX = log10(AB_2.get(i));
            double error = errorResistanceApparent.get(i) / 100f;
            double dotY = max(log10(resistanceApparent.get(i) + resistanceApparent.get(i) * error), 0);
            pointsSeries.getData().add(new XYChart.Data<>(dotX, dotY));
        }
        return pointsSeries;
    }

    protected static XYChart.Series<Double, Double> makeCurveErrorLower(ExperimentalData experimentalData) {
        final ArrayList<Double> AB_2 = new ArrayList<>(experimentalData.getAB_2());
        final ArrayList<Double> resistanceApparent = new ArrayList<>(experimentalData.getResistanceApparent());
        final ArrayList<Double> errorResistanceApparent = new ArrayList<>(experimentalData.getErrorResistanceApparent());

        XYChart.Series<Double, Double> pointsSeries = new XYChart.Series<>();
        for (int i = 0; i < experimentalData.getSize(); i++) {
            double dotX = log10(AB_2.get(i));
            double error = errorResistanceApparent.get(i) / 100f;
            double dotY = max(log10(resistanceApparent.get(i) - resistanceApparent.get(i) * error), 0);
            pointsSeries.getData().add(new XYChart.Data<>(dotX, dotY));
        }
        return pointsSeries;
    }
}
