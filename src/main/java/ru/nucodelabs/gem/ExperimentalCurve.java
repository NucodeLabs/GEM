package ru.nucodelabs.gem;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.List;

import static java.lang.Math.log10;
import static java.lang.Math.max;

public class ExperimentalCurve {

    protected static void makeCurve(
            LineChart<Double, Double> vesCurve,
            List<Double> AB_2,
            List<Double> resistanceApp,
            List<Double> errResistanceApp) {
        vesCurve.getData().clear();

        vesCurve.getData().add(makeCurveData(AB_2, resistanceApp));
        vesCurve.getData().add(makeCurveErrorUpper(AB_2, resistanceApp, errResistanceApp));
        vesCurve.getData().add(makeCurveErrorLower(AB_2, resistanceApp, errResistanceApp));
    }

    private static XYChart.Series<Double, Double> makeCurveData(List<Double> AB_2, List<Double> resistanceApp) {
        XYChart.Series<Double, Double> pointsSeries = new XYChart.Series<>();

        for (int i = 0; i < AB_2.size(); i++) {
            double dotX = log10(AB_2.get(i));
            double dotY = max(log10(resistanceApp.get(i)), 0);
            pointsSeries.getData().add(new XYChart.Data<>(dotX, dotY));
        }
        return pointsSeries;
    }

    protected static XYChart.Series<Double, Double> makeCurveErrorUpper(List<Double> AB_2, List<Double> resistanceApp, List<Double> errorResistanceApp) {
        XYChart.Series<Double, Double> pointsSeries = new XYChart.Series<>();

        for (int i = 0; i < AB_2.size(); i++) {
            double dotX = log10(AB_2.get(i));
            double error = errorResistanceApp.get(i) / 100f;
            double dotY = max(log10(resistanceApp.get(i) + resistanceApp.get(i) * error), 0);
            pointsSeries.getData().add(new XYChart.Data<>(dotX, dotY));
        }
        return pointsSeries;
    }

    protected static XYChart.Series<Double, Double> makeCurveErrorLower(List<Double> AB_2, List<Double> resistanceApp, List<Double> errorResistanceApp) {
        XYChart.Series<Double, Double> pointsSeries = new XYChart.Series<>();

        for (int i = 0; i < AB_2.size(); i++) {
            double dotX = log10(AB_2.get(i));
            double error = errorResistanceApp.get(i) / 100f;
            double dotY = max(log10(resistanceApp.get(i) - resistanceApp.get(i) * error), 0);
            pointsSeries.getData().add(new XYChart.Data<>(dotX, dotY));
        }
        return pointsSeries;
    }
}
