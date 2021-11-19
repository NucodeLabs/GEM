package ru.nucodelabs.gem;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import ru.nucodelabs.files.sonet.MODFile;
import ru.nucodelabs.files.sonet.STTFile;

import java.util.List;

import static java.lang.Math.log10;
import static java.lang.Math.max;

public class TheoreticalCurve {
    protected static List<Double> solvedResistance;

    protected static void makeCurve(LineChart<Double, Double> vesCurve, STTFile openedSTT, MODFile openedMOD) {
        if (vesCurve.getData().size() > AppController.EXP_CURVE_SERIES_CNT) {
            vesCurve.getData().remove(AppController.EXP_CURVE_SERIES_CNT);
        }

        try {
            solvedResistance = ForwardSolver.ves(openedMOD.getResistance(), openedMOD.getPower(), openedSTT.getAB_2());
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
        vesCurve.getData().add(makeCurveData(openedSTT, solvedResistance));
    }

    private static XYChart.Series<Double, Double> makeCurveData(STTFile openedSTT, List<Double> solvedResistance) {
        XYChart.Series<Double, Double> pointsSeries = new XYChart.Series<>();

        for (int i = 0; i < openedSTT.getAB_2().size(); i++) {
            double dotX = log10(openedSTT.getAB_2().get(i));
            double dotY = max(log10(solvedResistance.get(i)), 0);
            pointsSeries.getData().add(new XYChart.Data<>(dotX, dotY));
        }
        return pointsSeries;
    }
}
