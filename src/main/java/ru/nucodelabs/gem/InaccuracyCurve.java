package ru.nucodelabs.gem;

import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TitledPane;
import ru.nucodelabs.algorithms.ForwardSolver;
import ru.nucodelabs.algorithms.MisfitFunctions;
import ru.nucodelabs.data.ExperimentalData;
import ru.nucodelabs.data.ModelData;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class InaccuracyCurve {

    protected static void makeCurve(LineChart<Double, Double> inaccuracyCurve, TitledPane inaccuracyPane, ExperimentalData experimentalData, ModelData modelData) {
        inaccuracyCurve.getData().clear();
        ArrayList<Double> solvedResistance = new ArrayList<>(ForwardSolver.ves(
                modelData.getResistance(),
                modelData.getPower(),
                experimentalData.getAB_2()
        ));

        inaccuracyCurve.getData().addAll(makeCurveData(experimentalData, solvedResistance));
//        inaccuracyCurve.setCreateSymbols(false);
        colorizeSeries(inaccuracyCurve.getData());
        makeTextAvgMax(inaccuracyPane, inaccuracyCurve);
        inaccuracyCurve.setVisible(true);
    }

    protected static void makeTextAvgMax(TitledPane inaccuracyPane, LineChart<Double, Double> inaccuracyCurve) {
        inaccuracyPane.setText(
                String.format("%s | avg = %d%% | max = %d%%",
                        inaccuracyPane.getText().split("\s")[0],
                        round(calculateAverage(inaccuracyCurve.getData())),
                        round(calculateMax(inaccuracyCurve.getData())))
        );
    }

    protected static void colorizeSeries(ObservableList<XYChart.Series<Double, Double>> data) {
        data.forEach(
                s -> {
                    if (abs(s.getData().get(1).getYValue()) < 100f) {
                        s.getNode().setStyle("-fx-stroke: LimeGreen;");
                        s.getData().get(1).getNode().lookup(".chart-line-symbol").setStyle("-fx-background-color: LimeGreen");
                        s.getData().get(0).getNode().lookup(".chart-line-symbol").setStyle("-fx-background-color: LimeGreen");
                    }
                }
        );
    }

    protected static List<XYChart.Series<Double, Double>> makeCurveData(ExperimentalData experimentalData, List<Double> solvedResistance) {
        final ArrayList<Double> AB_2 = new ArrayList<>(experimentalData.getAB_2());
        final ArrayList<Double> resistanceApparent = new ArrayList<>(experimentalData.getResistanceApparent());
        final ArrayList<Double> errorResistanceApparent = new ArrayList<>(experimentalData.getErrorResistanceApparent());

        List<XYChart.Series<Double, Double>> res = new ArrayList<>();
        for (int i = 0; i < experimentalData.getSize(); i++) {
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

    protected static Double calculateAverage(ObservableList<XYChart.Series<Double, Double>> data) {
        return data.stream()
                .mapToDouble(
                        s -> abs(s.getData().get(1).getYValue())
                )
                .average()
                .orElse(0);
    }

    public static Double calculateMax(ObservableList<XYChart.Series<Double, Double>> data) {
        return data.stream()
                .mapToDouble(
                        s -> abs(s.getData().get(1).getYValue())
                )
                .max()
                .orElse(0);
    }
}
