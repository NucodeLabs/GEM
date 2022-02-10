package ru.nucodelabs.gem.view;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import ru.nucodelabs.data.ves.ModelData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.Math.log10;
import static java.lang.Math.pow;

/**
 * Enables drag-n-drop functionality on given line chart for step curve (Model Curve)
 */
public class ModelCurveDragger {

    private static final double TOLERANCE = 0.005;
    private final int MOD_CURVE_SERIES_INDEX;

    private final LineChart<Double, Double> vesCurvesLineChart;
    private final ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> vesCurvesData;
    private ModelData modelData;
    private final Map<XYChart.Data<Double, Double>, Integer> pointResistanceMap;
    private final Map<XYChart.Data<Double, Double>, Integer> pointPowerMap;

    // ends of line to be dragged
    private XYChart.Data<Double, Double> point1;
    private XYChart.Data<Double, Double> point2;

    // for vertical line dragging
    private Double leftLimitX;
    private Double rightLimitX;

    /**
     * Initializes dragger for given line chart
     *
     * @param vesCurvesLineChart line chart with model curve
     * @param vesCurvesData      data property that line chart bound to
     * @param modelCurveIndex    index of series of model curve in line chart's data list of series
     */
    public ModelCurveDragger(LineChart<Double, Double> vesCurvesLineChart,
                             ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> vesCurvesData,
                             int modelCurveIndex) {
        this.vesCurvesLineChart = vesCurvesLineChart;
        this.vesCurvesData = vesCurvesData;
        pointResistanceMap = new HashMap<>();
        pointPowerMap = new HashMap<>();
        MOD_CURVE_SERIES_INDEX = modelCurveIndex;
    }

    /**
     * Detects two points that will be dragged by mouse
     *
     * @param mouseEvent mouse pressed event
     */
    public void lineToDragDetector(MouseEvent mouseEvent) {
        Point2D pointInScene = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());

        Double mouseX = vesCurvesLineChart.getXAxis().getValueForDisplay(
                vesCurvesLineChart.getXAxis().sceneToLocal(pointInScene).getX()
        );

        var points = vesCurvesData.get().get(MOD_CURVE_SERIES_INDEX).getData();
        var closestVerticalLines = points.stream()
                .filter(p -> p.getXValue() > mouseX - TOLERANCE && p.getXValue() < mouseX + TOLERANCE)
                .toList();

        if (closestVerticalLines.size() == 2) {
            point1 = closestVerticalLines.get(0);
            point2 = closestVerticalLines.get(1);

            for (var point : points) {
                if (point.getXValue() < mouseX && point.getXValue() < point1.getXValue()) {
                    leftLimitX = point.getXValue();
                }
                if (point.getXValue() > mouseX && point.getXValue() > point2.getXValue()) {
                    rightLimitX = point.getXValue();
                    break;
                }
            }
        } else {
            for (var point : points) {
                if (point.getXValue() < mouseX) {
                    point1 = point;
                }
                if (point.getXValue() > mouseX) {
                    point2 = point;
                    break;
                }
            }
        }
    }

    /**
     * Drags the points by mouse and modifies ModelData values if called initModelData() previously
     *
     * @param mouseEvent mouse dragged event
     */
    public void dragHandler(MouseEvent mouseEvent) {
        Point2D pointInScene = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());

        Double mouseX = vesCurvesLineChart.getXAxis().getValueForDisplay(
                vesCurvesLineChart.getXAxis().sceneToLocal(pointInScene).getX()
        );
        Double mouseY = vesCurvesLineChart.getYAxis().getValueForDisplay(
                vesCurvesLineChart.getYAxis().sceneToLocal(pointInScene).getY()
        );

        if (point1 != null && point2 != null) {
            if (Objects.equals(point1.getXValue(), point2.getXValue())
                    && leftLimitX < mouseX - TOLERANCE * 2 && mouseX + TOLERANCE * 2 < rightLimitX) {
                point1.setXValue(mouseX);
                point2.setXValue(mouseX);
            } else if (Objects.equals(point1.getYValue(), point2.getYValue())) {
                point1.setYValue(mouseY);
                point2.setYValue(mouseY);
                if (modelData != null) {
                    modelData.getResistance().set(
                            pointResistanceMap.get(point1), pow(10, point1.getYValue())
                    );
                }
            }
        }
    }

    /**
     * Initializes ModelData that will be modified by dragging points
     *
     * @param modelData model data that match curve
     */
    public void initModelData(ModelData modelData) {
        pointResistanceMap.clear();
        pointPowerMap.clear();
        this.modelData = modelData;
        var points = vesCurvesData.get().get(MOD_CURVE_SERIES_INDEX).getData();
        for (var point : points) {
            var resistance = modelData.getResistance();
            for (int i = 0; i < resistance.size(); i++) {
                if (point.getYValue() == log10(resistance.get(i))) {
                    pointResistanceMap.put(point, i);
                }
            }
        }
        if (pointResistanceMap.values().stream().distinct().count() != modelData.getResistance().size()) {
            throw new IllegalArgumentException("ModelData does not match series");
        }
    }
}
