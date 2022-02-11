package ru.nucodelabs.gem.view;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import ru.nucodelabs.data.ves.ModelData;

import java.util.*;

import static java.lang.Math.log10;
import static java.lang.Math.pow;

/**
 * Enables drag-n-drop functionality on given line chart for step curve (Model Curve)
 */
public class ModelCurveDragger {

    private static final double TOLERANCE = 0.01;
    private final int MOD_CURVE_SERIES_INDEX;

    private final LineChart<Double, Double> vesCurvesLineChart;
    private final ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> vesCurvesData;
    private ModelData modelData;

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
                double diff = mouseX - point1.getXValue();
                point1.setXValue(mouseX);
                point2.setXValue(mouseX);
                if (modelData != null) {
                    int index1 = pointPowerMap.get(point1);
                    int index2 = index1 + 1; // neighbor
                    double initialValue1 = modelData.getPower().get(index1);
                    double initialValue2 = modelData.getPower().get(index2);
                    double newValue1 = pow(10, log10(initialValue1) - diff);
                    double newValue2 = pow(10, log10(initialValue2) + diff);
                    modelData.getPower().set(index1, newValue1);
                    modelData.getPower().set(index2, newValue2);
                }
            } else if (Objects.equals(point1.getYValue(), point2.getYValue())) {
                point1.setYValue(mouseY);
                point2.setYValue(mouseY);
                if (modelData != null) {
                    int index = pointResistanceMap.get(point1);
                    double newValue = pow(10, mouseY);
                    modelData.getResistance().set(index, newValue);
                }
                // sample
            }
        }
    }

    /**
     * Call this if array structure will change
     */
    private void updateMappings() {
        initModelData(modelData);
    }

    /**
     * Initializes ModelData that will be modified by dragging points
     *
     * @param modelData model data that match curve
     */
    public void initModelData(ModelData modelData) {
        String E_MSG = "ModelData array size: %d does not match mapping size: %d";

        pointResistanceMap = new HashMap<>();
        this.modelData = modelData;
        var points = vesCurvesData.get().get(MOD_CURVE_SERIES_INDEX).getData();
        var resistance = modelData.getResistance();
        for (var point : points) {
            for (int i = 0; i < resistance.size(); i++) {
                if (point.getYValue() == log10(resistance.get(i))) {
                    pointResistanceMap.put(point, i);
                }
            }
        }

        if (pointResistanceMap.values().stream().distinct().count() != modelData.getResistance().size()) {
            throw new IllegalArgumentException(
                    String.format(E_MSG,
                            pointResistanceMap.values().stream().distinct().count(),
                            modelData.getResistance().size()
                    )
            );
        }

        pointPowerMap = new HashMap<>();
        var power = modelData.getPower();
        double currentHeight = 0;
        List<Double> height = new ArrayList<>();

        for (Double p : power) {
            height.add(currentHeight += p);
        }
        height.remove(height.size() - 1); // last in power is zero

        for (var point : points) {
            for (int i = 0; i < height.size(); i++) {
                if (point.getXValue() == log10(height.get(i))) {
                    pointPowerMap.put(point, i);
                }
            }
        }

        if (pointPowerMap.values().stream().distinct().count() != modelData.getPower().size() - 1) {
            throw new IllegalArgumentException(
                    String.format(E_MSG,
                            pointPowerMap.values().stream().distinct().count(),
                            modelData.getPower().size()
                    )
            );
        }
    }
}
