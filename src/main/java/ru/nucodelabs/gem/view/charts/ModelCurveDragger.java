package ru.nucodelabs.gem.view.charts;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import ru.nucodelabs.data.ves.ModelData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.lang.Math.pow;

/**
 * Enables drag-n-drop functionality on given line chart for step curve (Model Curve)
 */
public class ModelCurveDragger {

    private static final double TOLERANCE_ABS = 2;
    private final int MOD_CURVE_SERIES_INDEX;

    private final Function<Point2D, XYChart.Data<Double, Double>> coordinatesInSceneToValue;
    private final ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> vesCurvesData;

    // mapping: point on chart --> index of the value in data model arrays
    private Map<XYChart.Data<Double, Double>, Integer> pointResistanceMap;
    private Map<XYChart.Data<Double, Double>, Integer> pointPowerMap;

    // ends of line to be dragged
    private XYChart.Data<Double, Double> point1;
    private XYChart.Data<Double, Double> point2;

    // for vertical line dragging
    private Double leftLimitX;
    private Double rightLimitX;

    /**
     * Adds drag-n-drop functionality for model step chart.
     * Modifies values of given property and ModelData if initialized.
     *
     * @param coordinatesInSceneToValue line chart node dependent function to convert X and Y to values for axis
     * @param chartData                 data property of line chart or bound
     * @param modelCurveIndex           index of series in data
     */
    public ModelCurveDragger(
            Function<Point2D, XYChart.Data<Double, Double>> coordinatesInSceneToValue,
            ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> chartData,
            int modelCurveIndex) {
        this.coordinatesInSceneToValue = coordinatesInSceneToValue;
        this.vesCurvesData = chartData;
        MOD_CURVE_SERIES_INDEX = modelCurveIndex;
    }

    /**
     * Detects two points that will be dragged by mouse
     *
     * @param mouseEvent mouse pressed event
     */
    public void detectPoints(MouseEvent mouseEvent) {

        Double mouseX = coordinatesToValues(mouseEvent).getXValue();

        Double mouseXLeftBound = coordinatesToValues(
                new Point2D(mouseEvent.getSceneX() - TOLERANCE_ABS, mouseEvent.getSceneY())
        ).getXValue();
        Double mouseXRightBound = coordinatesToValues(
                new Point2D(mouseEvent.getSceneX() + TOLERANCE_ABS, mouseEvent.getSceneY())
        ).getXValue();

        var points = vesCurvesData.get().get(MOD_CURVE_SERIES_INDEX).getData();
        var closestVerticalLines = points.stream()
                .filter(p -> mouseXLeftBound < p.getXValue() && p.getXValue() < mouseXRightBound)
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

    public void setStyle() {
        if (point1 != null && point2 != null) {
            String style = """
                    -fx-background-color: blue;
                    """;
            point1.getNode().lookup(".chart-line-symbol").setStyle(style);
            point2.getNode().lookup(".chart-line-symbol").setStyle(style);
        }
    }

    /**
     * Drags the points by mouse and modifies ModelData values and returns same instance
     *
     * @param mouseEvent mouse dragged event
     * @param modelData  model data
     */
    public ModelData dragHandler(MouseEvent mouseEvent, ModelData modelData) {
        mapModelData(modelData);
        var valuesForAxis = coordinatesToValues(mouseEvent);
        Double mouseX = valuesForAxis.getXValue();
        Double mouseY = valuesForAxis.getYValue();

        Double mouseXLeftBound = coordinatesToValues(
                new Point2D(mouseEvent.getSceneX() - TOLERANCE_ABS, mouseEvent.getSceneY())
        ).getXValue();
        Double mouseXRightBound = coordinatesToValues(
                new Point2D(mouseEvent.getSceneX() + TOLERANCE_ABS, mouseEvent.getSceneY())
        ).getXValue();


        if (point1 != null && point2 != null) {
            if (Objects.equals(point1.getXValue(), point2.getXValue())
                    && leftLimitX < mouseXLeftBound && mouseXRightBound < rightLimitX) {
                double diff = pow(10, mouseX) - pow(10, point1.getXValue());
                point1.setXValue(mouseX);
                point2.setXValue(mouseX);
                if (modelData != null) {
                    int index1 = pointPowerMap.get(point1);
                    int index2 = index1 + 1; // neighbor
                    double initialValue1 = modelData.power().get(index1);
                    double initialValue2 = modelData.power().get(index2);
                    double newValue1 = initialValue1 + diff;
                    double newValue2 = initialValue2 - diff;
                    modelData.power().set(index1, newValue1);
                    if (index2 != modelData.size() - 1) {
                        modelData.power().set(index2, newValue2);
                    }
                }
            } else if (Objects.equals(point1.getYValue(), point2.getYValue())) {
                point1.setYValue(mouseY);
                point2.setYValue(mouseY);
                int index = pointResistanceMap.get(point1);
                double newValue = pow(10, mouseY);
                modelData.resistance().set(index, newValue);
            }
        }
        return modelData;
    }

    /**
     * Maps ModelData that will be modified by dragging points
     *
     * @param modelData model data that match curve
     */
    private void mapModelData(ModelData modelData) {
        String E_MSG = "ModelData array size: %d does not match mapping size: %d";

        pointResistanceMap = new HashMap<>();
        var points = vesCurvesData.get().get(MOD_CURVE_SERIES_INDEX).getData();
        for (int i = 0, j = 0; i < points.size(); i += 2, j++) {
            pointResistanceMap.put(points.get(i), j);
            pointResistanceMap.put(points.get(i + 1), j);
        }

        if (pointResistanceMap.values().stream().distinct().count() != modelData.resistance().size()) {
            throw new IllegalArgumentException(
                    String.format(E_MSG,
                            pointResistanceMap.values().stream().distinct().count(),
                            modelData.resistance().size()
                    )
            );
        }

        pointPowerMap = new HashMap<>();
        for (int i = 1, j = 0; i < points.size() - 1; i += 2, j++) {
            pointPowerMap.put(points.get(i), j);
            pointPowerMap.put(points.get(i + 1), j);
        }

        if (pointPowerMap.values().stream().distinct().count() != modelData.power().size() - 1) {
            throw new IllegalArgumentException(
                    String.format(E_MSG,
                            pointPowerMap.values().stream().distinct().count(),
                            modelData.power().size()
                    )
            );
        }
    }

    /**
     * Converts mouse event coordinates to valid values for axis
     *
     * @param mouseEvent mouse pressed/dragged event
     * @return point with valid X and Y values
     */
    private XYChart.Data<Double, Double> coordinatesToValues(MouseEvent mouseEvent) {
        Point2D pointInScene = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());

        return coordinatesToValues(pointInScene);
    }

    private XYChart.Data<Double, Double> coordinatesToValues(Point2D pointInScene) {
        return coordinatesInSceneToValue.apply(pointInScene);
    }

    public void resetStyle() {
        if (point1 != null && point2 != null) {
            String style = """
                     -fx-background-color: transparent;
                    """;
            point1.getNode().lookup(".chart-line-symbol").setStyle(style);
            point2.getNode().lookup(".chart-line-symbol").setStyle(style);
        }
    }
}