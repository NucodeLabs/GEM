package ru.nucodelabs.gem.charts;

import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import ru.nucodelabs.data.ModelData;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static java.lang.Math.log10;
import static ru.nucodelabs.gem.charts.VESCurve.*;

public class ModelCurve {
    protected static void addData(LineChart<Double, Double> vesCurve, ModelData modelData) {
        vesCurve.getData().add(makeCurveData(modelData));
    }

    protected static void initializeWithData(LineChart<Double, Double> vesCurve, ModelData modelData) {
        if (vesCurve.getData().size() > THEOR_CURVE_SERIES_CNT) {
            vesCurve.setData(vesCurve.getData().stream().limit(THEOR_CURVE_SERIES_CNT)
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        }
        vesCurve.getXAxis().setAutoRanging(false);
        addData(vesCurve, modelData);

        XYChart.Series<Double, Double> series = vesCurve.getData().get(MOD_CURVE_SERIES_CNT - 1);
        Axis<Double> xAxis = vesCurve.getXAxis();
        Axis<Double> yAxis = vesCurve.getYAxis();

        for (XYChart.Data<Double, Double> data : series.getData()) {
            Node node = data.getNode();
            node.setCursor(Cursor.HAND);
            node.setOnMouseDragged(e -> {
                Point2D pointInScene = new Point2D(e.getSceneX(), e.getSceneY());
                double xAxisLoc = xAxis.sceneToLocal(pointInScene).getX();
                double yAxisLoc = yAxis.sceneToLocal(pointInScene).getY();
                Double x = xAxis.getValueForDisplay(xAxisLoc);
                Double y = yAxis.getValueForDisplay(yAxisLoc);
                data.setXValue(x);
                data.setYValue(y);
            });
        }
    }

    private static XYChart.Series<Double, Double> makeCurveData(ModelData modelData) {
        XYChart.Series<Double, Double> series = new XYChart.Series<>();
        final ArrayList<Double> resistance = new ArrayList<>(modelData.getResistance());
        final ArrayList<Double> power = new ArrayList<>(modelData.getPower());

        series.getData().add(
                new XYChart.Data<>(
                        log10(0 + EPSILON),
                        log10(resistance.get(0))
                )
        );

        Double prevSum = 0d;
        for (int i = 0; i < resistance.size() - 1; i++) {
            final Double currentResistance = resistance.get(i);
            final Double currentPower = power.get(i);

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


