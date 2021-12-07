package ru.nucodelabs.gem;

import javafx.collections.FXCollections;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import ru.nucodelabs.algorithms.ForwardSolver;
import ru.nucodelabs.data.ExperimentalData;
import ru.nucodelabs.data.ModelData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.log10;
import static java.lang.Math.max;

public class TheoreticalCurve {
    protected static void makeCurve(LineChart<Double, Double> vesCurve, ExperimentalData experimentalData, ModelData modelData) {

        if (vesCurve.getData().size() > AppController.EXP_CURVE_SERIES_CNT) {
            vesCurve.setData(vesCurve.getData().stream().limit(AppController.EXP_CURVE_SERIES_CNT)
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        }

        ArrayList<Double> solvedResistance = new ArrayList<>(ForwardSolver.ves(
                modelData.getResistance(),
                modelData.getPower(),
                experimentalData.getAB_2()
        ));


        vesCurve.getData().add(makeCurveData(experimentalData, solvedResistance));
    }

    private static XYChart.Series<Double, Double> makeCurveData(ExperimentalData experimentalData, List<Double> solvedResistance) {
        final ArrayList<Double> AB_2 = new ArrayList<>(experimentalData.getAB_2());

        XYChart.Series<Double, Double> pointsSeries = new XYChart.Series<>();
        for (int i = 0; i < AB_2.size(); i++) {
            double dotX = log10(AB_2.get(i));
            double dotY = max(log10(solvedResistance.get(i)), 0);
            pointsSeries.getData().add(new XYChart.Data<>(dotX, dotY));
        }
        return pointsSeries;
    }
}
