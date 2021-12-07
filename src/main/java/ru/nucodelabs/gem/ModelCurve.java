package ru.nucodelabs.gem;

import javafx.collections.FXCollections;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import ru.nucodelabs.data.ModelData;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static java.lang.Math.log10;

public class ModelCurve {
    protected static void makeCurve(LineChart<Double, Double> vesCurve, ModelData modelData) {
        if (vesCurve.getData().size() > AppController.THEOR_CURVE_SERIES_CNT) {
            vesCurve.setData(vesCurve.getData().stream().limit(AppController.THEOR_CURVE_SERIES_CNT)
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        }
        vesCurve.getXAxis().setAutoRanging(false);
        vesCurve.getData().add(makeCurveData(modelData));
    }

    private static XYChart.Series<Double, Double> makeCurveData(ModelData modelData) {
        XYChart.Series<Double, Double> series = new XYChart.Series<>();
        final ArrayList<Double> resistance = new ArrayList<>(modelData.getResistance());
        final ArrayList<Double> power = new ArrayList<>(modelData.getPower());

        series.getData().add(
                new XYChart.Data<>(
                        -100d,
                        log10(resistance.get(0))
                )
        );

        Double prevSum = 0d;
        for (int i = 0; i < resistance.size() - 1; i++) {
            Double currentResistance = resistance.get(i);
            Double currentPower = power.get(i);

            series.getData().add(
                    new XYChart.Data<>(
                            log10(currentPower + prevSum),
                            log10(currentResistance)
                    )
            );

            Double nextResistance = resistance.get(i + 1);
            series.getData().add(
                    new XYChart.Data<>(
                            log10(currentPower + prevSum),
                            log10(nextResistance)
                    )
            );
            prevSum += currentPower;
        }

        series.getData().add(
                new XYChart.Data<>(
                        100d,
                        log10(resistance.get(resistance.size() - 1))
                )
        );
        return series;
    }
}


