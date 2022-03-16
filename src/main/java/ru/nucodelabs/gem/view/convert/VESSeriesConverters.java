package ru.nucodelabs.gem.view.convert;

import javafx.scene.chart.XYChart;
import ru.nucodelabs.algorithms.ForwardSolver;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.log10;
import static java.lang.Math.max;

/**
 * Functions that convert VES data to series for charts.
 */
public class VESSeriesConverters {

    private VESSeriesConverters() {
    }

    private enum BoundType {UPPER_BOUND, LOWER_BOUND}

    public static XYChart.Series<Double, Double> toExperimentalCurveSeries(final ExperimentalData experimentalData) {
        XYChart.Series<Double, Double> experimentalCurveSeries = new XYChart.Series<>();
        for (int i = 0; i < experimentalData.getSize(); i++) {
            double dotX = log10(experimentalData.ab_2().get(i));
            double dotY = max(log10(experimentalData.resistanceApparent().get(i)), 0);

            experimentalCurveSeries.getData().add(new XYChart.Data<>(dotX, dotY));
        }

        return experimentalCurveSeries;
    }

    public static XYChart.Series<Double, Double> toErrorExperimentalCurveUpperBoundSeries(final ExperimentalData experimentalData) {
        return toErrorBoundSeries(experimentalData, BoundType.UPPER_BOUND);
    }

    public static XYChart.Series<Double, Double> toErrorExperimentalCurveLowerBoundSeries(final ExperimentalData experimentalData) {
        return toErrorBoundSeries(experimentalData, BoundType.LOWER_BOUND);
    }

    private static XYChart.Series<Double, Double> toErrorBoundSeries(final ExperimentalData experimentalData, BoundType boundType) {
        XYChart.Series<Double, Double> series = new XYChart.Series<>();
        final int size = experimentalData.getSize();
        final List<Double> ab_2 = experimentalData.ab_2();
        final List<Double> errorResistanceApparent = experimentalData.errorResistanceApparent();
        final List<Double> resistanceApparent = experimentalData.resistanceApparent();

        for (int i = 0; i < size; i++) {
            double dotX = log10(ab_2.get(i));
            double error = errorResistanceApparent.get(i) / 100f;
            double dotY;
            if (boundType == BoundType.UPPER_BOUND) {
                dotY = max(
                        log10(resistanceApparent.get(i) + resistanceApparent.get(i) * error), 0);
            } else {
                dotY = max(
                        log10(resistanceApparent.get(i) - resistanceApparent.get(i) * error), 0);
            }

            series.getData().add(new XYChart.Data<>(dotX, dotY));
        }

        return series;
    }

    public static XYChart.Series<Double, Double> toTheoreticalCurveSeries(final ExperimentalData experimentalData, final ModelData modelData) {
        XYChart.Series<Double, Double> theoreticalCurveSeries = new XYChart.Series<>();
        final List<Double> ab_2 = experimentalData.ab_2();
        final List<Double> resistance = modelData.resistance();
        final List<Double> power = modelData.power();
        final int size = experimentalData.getSize();

        ArrayList<Double> solvedResistance = new ArrayList<>(ForwardSolver.ves(
                resistance,
                power,
                ab_2
        ));

        for (int i = 0; i < size; i++) {
            double dotX = log10(ab_2.get(i));
            double dotY = max(
                    log10(solvedResistance.get(i)),
                    0
            );
            theoreticalCurveSeries.getData().add(new XYChart.Data<>(dotX, dotY));
        }

        return theoreticalCurveSeries;
    }

    public static XYChart.Series<Double, Double> toModelCurveSeries(ModelData modelData) {
        final double FIRST_X = 1e-2;
        final double LAST_X = 1e100;

        XYChart.Series<Double, Double> modelCurveSeries = new XYChart.Series<>();
        final List<Double> power = modelData.power();
        final List<Double> resistance = modelData.resistance();

        // first point
        modelCurveSeries.getData().add(
                new XYChart.Data<>(
                        log10(FIRST_X),
                        log10(resistance.get(0))
                )
        );

        Double prevSum = 0d;
        for (int i = 0; i < resistance.size() - 1; i++) {
            final Double currentResistance = resistance.get(i);
            final Double currentPower = power.get(i);

            modelCurveSeries.getData().add(
                    new XYChart.Data<>(
                            log10(currentPower + prevSum),
                            log10(currentResistance)
                    )
            );

            Double nextResistance = resistance.get(i + 1);
            modelCurveSeries.getData().add(
                    new XYChart.Data<>(
                            log10(currentPower + prevSum),
                            log10(nextResistance)
                    )
            );
            prevSum += currentPower;
        }

        // last point
        final int lastResistanceIndex = resistance.size() - 1;
        modelCurveSeries.getData().add(
                new XYChart.Data<>(
                        log10(LAST_X),
                        log10(resistance.get(lastResistanceIndex))
                )
        );

        return modelCurveSeries;
    }
}
