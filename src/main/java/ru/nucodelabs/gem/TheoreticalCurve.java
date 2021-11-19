package ru.nucodelabs.gem;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;

import java.util.List;

import static java.lang.Math.log10;
import static java.lang.Math.max;

public class TheoreticalCurve {
    private static List<Double> solvedResistance = null;

    public static List<Double> getSolvedResistance() {
        return solvedResistance;
    }

    protected static void makeCurve(
            LineChart<Double, Double> vesCurve,
            List<Double> AB_2,
            List<Double> resistance,
            List<Double> power) {
        if (vesCurve.getData().size() > AppController.EXP_CURVE_SERIES_CNT) {
            vesCurve.getData().remove(AppController.EXP_CURVE_SERIES_CNT);
        }

        try {
            solvedResistance = ForwardSolver.ves(resistance, power, AB_2);
        } catch (UnsatisfiedLinkError e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Невозможно решить прямую задачу");
            alert.setHeaderText("Отсутствует библиотека ForwardSolver");
            alert.setContentText("""
                    Убедитесь в наличии файла с именем
                    - Windows: forwardsolver.dll
                    - macOS: libforwardsolver.dylib
                    - Linux: libforwardsolver.so
                    """);
            alert.show();
            return;
        }
        vesCurve.getData().add(makeCurveData(AB_2, solvedResistance));
    }

    private static XYChart.Series<Double, Double> makeCurveData(List<Double> AB_2, List<Double> solvedResistance) {
        XYChart.Series<Double, Double> pointsSeries = new XYChart.Series<>();

        for (int i = 0; i < AB_2.size(); i++) {
            double dotX = log10(AB_2.get(i));
            double dotY = max(log10(solvedResistance.get(i)), 0);
            pointsSeries.getData().add(new XYChart.Data<>(dotX, dotY));
        }
        return pointsSeries;
    }
}
