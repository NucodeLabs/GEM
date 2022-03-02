package ru.nucodelabs.gem.view;

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

    public static XYChart.Series<Double, Double> toExperimentalCurveSeries(final ExperimentalData experimentalData) {
        XYChart.Series<Double, Double> experimentalCurveSeries = new XYChart.Series<>();
        for (int i = 0; i < experimentalData.getSize(); i++) {
            double dotX = log10(experimentalData.ab_2().get(i));
            double dotY = max(log10(experimentalData.resistanceApparent().get(i)), 0);

            experimentalCurveSeries.getData().add(new XYChart.Data<>(dotX, dotY));
        }

        experimentalCurveSeries.setName("Экспериментальная кривая"); //TODO Использовать UI.properties, переместить в MainViewModel
        return experimentalCurveSeries;
    }

    public static XYChart.Series<Double, Double> toErrorExperimentalCurveUpperBoundSeries(final ExperimentalData experimentalData) {
        XYChart.Series<Double, Double> errorExperimentalCurveUpperBoundSeries = new XYChart.Series<>();
        final int size = experimentalData.getSize();
        final List<Double> ab_2 = experimentalData.ab_2();
        final List<Double> errorResistanceApparent = experimentalData.errorResistanceApparent();
        final List<Double> resistanceApparent = experimentalData.resistanceApparent();

        for (int i = 0; i < size; i++) {
            double dotX = log10(ab_2.get(i));
            double error = errorResistanceApparent.get(i) / 100f;
            double dotY = max(
                    log10(resistanceApparent.get(i) + resistanceApparent.get(i) * error),
                    0
            );

            errorExperimentalCurveUpperBoundSeries.getData().add(new XYChart.Data<>(dotX, dotY));
        }

        errorExperimentalCurveUpperBoundSeries.setName("Верхняя граница погрешности");
        return errorExperimentalCurveUpperBoundSeries;
    }

    public static XYChart.Series<Double, Double> toErrorExperimentalCurveLowerBoundSeries(final ExperimentalData experimentalData) {
        XYChart.Series<Double, Double> errorExperimentalCurveLowerBoundSeries = new XYChart.Series<>();
        final int size = experimentalData.getSize();
        final List<Double> ab_2 = experimentalData.ab_2();
        final List<Double> errorResistanceApparent = experimentalData.errorResistanceApparent();
        final List<Double> resistanceApparent = experimentalData.resistanceApparent();

        for (int i = 0; i < size; i++) {
            double dotX = log10(ab_2.get(i));
            double error = errorResistanceApparent.get(i) / 100f;
            double dotY = max(
                    log10(
                            resistanceApparent.get(i) - resistanceApparent.get(i) * error
                    ),
                    0
            );

            errorExperimentalCurveLowerBoundSeries.getData().add(new XYChart.Data<>(dotX, dotY));
        }

        errorExperimentalCurveLowerBoundSeries.setName("Нижняя граница погрешности");
        return errorExperimentalCurveLowerBoundSeries;
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

        theoreticalCurveSeries.setName("Теоретическая кривая");
        return theoreticalCurveSeries;
    }

    public static XYChart.Series<Double, Double> toModelCurveSeries(ModelData modelData) {
        XYChart.Series<Double, Double> modelCurveSeries = new XYChart.Series<>();
        final List<Double> power = modelData.power();
        final List<Double> resistance = modelData.resistance();

//        first point
        modelCurveSeries.getData().add(
                new XYChart.Data<>(
                        log10(1e-3),
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

//        last point
        final int lastResistanceIndex = resistance.size() - 1;
        modelCurveSeries.getData().add(
                new XYChart.Data<>(
                        100d,
                        log10(resistance.get(lastResistanceIndex))
                )
        );

        modelCurveSeries.setName("Кривая модели");
        return modelCurveSeries;
    }
}
