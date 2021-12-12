package ru.nucodelabs.gem.charts;

import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TitledPane;
import ru.nucodelabs.algorithms.ForwardSolver;
import ru.nucodelabs.algorithms.MisfitFunctions;
import ru.nucodelabs.data.Picket;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

/**
 * <code>InaccuracyStacks</code> object drawing inaccuracy stacks using <code>LineChart</code>
 */
public class InaccuracyStacks {
    private final LineChart<Double, Double> inaccuracyLineChart;
    private final TitledPane inaccuracyPane;
    private final Picket picket;

    /**
     * <code>InaccuracyStacks</code> object drawing inaccuracy stacks using <code>LineChart inaccuracyLineChart</code>
     *
     * @param inaccuracyLineChart chart to draw on
     * @param inaccuracyPane      parent of chart. Represents additional information.
     * @param picket              picket with data
     */
    public InaccuracyStacks(LineChart<Double, Double> inaccuracyLineChart, TitledPane inaccuracyPane, Picket picket) {
        this.inaccuracyLineChart = inaccuracyLineChart;
        this.inaccuracyPane = inaccuracyPane;
        this.picket = picket;
    }

    public void createInaccuracyStacks() {
        List<XYChart.Series<Double, Double>> inaccuracySeriesList = getInaccuracySeriesList();

        inaccuracyLineChart.getData().clear();

        inaccuracyLineChart.getData().addAll(inaccuracySeriesList);
        colorizeSeries(inaccuracyLineChart.getData());
        makeTextAvgMax(inaccuracyPane, inaccuracyLineChart.getData());
        inaccuracyLineChart.setVisible(true);
    }

    private static void makeTextAvgMax(TitledPane inaccuracyPane, ObservableList<XYChart.Series<Double, Double>> data) {
        inaccuracyPane.setText(
                String.format("%s | avg = %d%% | max = %d%%",
                        inaccuracyPane.getText().split("\s")[0],
                        round(calculateAverage(data)),
                        round(calculateMax(data))
                )
        );
    }

    private static void colorizeSeries(ObservableList<XYChart.Series<Double, Double>> data) {
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

    private List<XYChart.Series<Double, Double>> getInaccuracySeriesList() {
        ArrayList<Double> solvedResistance = new ArrayList<>(ForwardSolver.ves(
                picket.getModelData().getResistance(),
                picket.getModelData().getPower(),
                picket.getExperimentalData().getAB_2()
        ));

        List<XYChart.Series<Double, Double>> res = new ArrayList<>();
        for (int i = 0; i < picket.getExperimentalData().getSize(); i++) {
            XYChart.Series<Double, Double> pointsSeries = new XYChart.Series<>();
            pointsSeries.getData().add(new XYChart.Data<>(
                    log10(picket.getExperimentalData().getAB_2().get(i)),
                    0d));
            double dotX = log10(picket.getExperimentalData().getAB_2().get(i));
            double dotY = abs(MisfitFunctions.calculateRelativeDeviationWithError(
                    picket.getExperimentalData().getResistanceApparent().get(i),
                    picket.getExperimentalData().getErrorResistanceApparent().get(i) / 100f,
                    solvedResistance.get(i)
            )) * signum(solvedResistance.get(i) - picket.getExperimentalData().getResistanceApparent().get(i)) * 100f;
            pointsSeries.getData().add(new XYChart.Data<>(dotX, dotY));
            res.add(pointsSeries);
        }

        return res;
    }

    private static Double calculateAverage(ObservableList<XYChart.Series<Double, Double>> data) {
        return data.stream()
                .mapToDouble(
                        s -> abs(s.getData().get(1).getYValue())
                )
                .average()
                .orElse(0);
    }

    private static Double calculateMax(ObservableList<XYChart.Series<Double, Double>> data) {
        return data.stream()
                .mapToDouble(
                        s -> abs(s.getData().get(1).getYValue())
                )
                .max()
                .orElse(0);
    }
}
