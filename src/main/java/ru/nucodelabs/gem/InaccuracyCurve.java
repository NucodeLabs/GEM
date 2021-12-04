package ru.nucodelabs.gem;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import ru.nucodelabs.algorithms.ForwardSolver;
import ru.nucodelabs.algorithms.MisfitFunctions;
import ru.nucodelabs.data.ExperimentalData;
import ru.nucodelabs.data.ModelData;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class InaccuracyCurve {

    protected static void makeCurve(
            LineChart<Double, Double> inaccuracyCurve, ExperimentalData experimentalData, ModelData modelData) {
        inaccuracyCurve.getData().clear();

        ArrayList<Double> solvedResistance = new ArrayList<>(ForwardSolver.ves(
                modelData.getResistance(),
                modelData.getPower(),
                experimentalData.getAB_2()
        ));

        inaccuracyCurve.getData().addAll(makeCurveData(experimentalData.getAB_2(), experimentalData.getResistanceApparent(), experimentalData.getErrorResistanceApparent(), solvedResistance));
//        inaccuracyCurve.setCreateSymbols(false);
        if (!inaccuracyCurve.isVisible()) {
            inaccuracyCurve.setVisible(true);
        }
    }

    protected static List<XYChart.Series<Double, Double>> makeCurveData(
            List<Double> AB_2,
            List<Double> resistanceApparent,
            List<Double> errorResistanceApparent,
            List<Double> solvedResistance) {
        List<XYChart.Series<Double, Double>> res = new ArrayList<>();

        for (int i = 0; i < AB_2.size(); i++) {
            XYChart.Series<Double, Double> pointsSeries = new XYChart.Series<>();
            pointsSeries.getData().add(new XYChart.Data<>(
                    log10(AB_2.get(i)),
                    0d));
            double dotX = log10(AB_2.get(i));
            double dotY = abs(MisfitFunctions.calculateRelativeDeviationWithError(
                    resistanceApparent.get(i),
                    errorResistanceApparent.get(i) / 100f,
                    solvedResistance.get(i)
            )) * signum(solvedResistance.get(i) - resistanceApparent.get(i)) * 100f;
            pointsSeries.getData().add(new XYChart.Data<>(dotX, dotY));
            res.add(pointsSeries);
        }

        return res;
    }
}
