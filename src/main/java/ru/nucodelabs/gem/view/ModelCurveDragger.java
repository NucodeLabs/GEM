package ru.nucodelabs.gem.view;

import javafx.geometry.Point2D;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;

import java.util.Objects;

import static java.lang.Math.min;

public class ModelCurveDragger {

    private final int MOD_CURVE_SERIES_INDEX = 4;

    private LineChart<Double, Double> vesCurvesLineChart;

    // ends of line to be dragged
    private XYChart.Data<Double, Double> point1;
    private XYChart.Data<Double, Double> point2;

    public ModelCurveDragger(LineChart<Double, Double> vesCurvesLineChart) {
        this.vesCurvesLineChart = vesCurvesLineChart;
    }

    public void lineToDragDetector(MouseEvent mouseEvent) {
        Point2D pointInScene = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());

        Double mouseX = vesCurvesLineChart.getXAxis().getValueForDisplay(
                vesCurvesLineChart.getXAxis().sceneToLocal(pointInScene).getX()
        );

        var points = vesCurvesLineChart.getData().get(MOD_CURVE_SERIES_INDEX).getData();
        var closestVerticalLines = points.stream()
                .filter(p -> p.getXValue() > mouseX - 0.01 && p.getXValue() < mouseX + 0.01)
                .toList();

        if (closestVerticalLines.size() >= 2 && closestVerticalLines.size() % 2 == 0) {
            Double closestLeftX = null;
            Double closestRightX = null;
            for (var point : closestVerticalLines) {
                if (point.getXValue() <= mouseX) {
                    closestLeftX = point.getXValue();
                }
                if (point.getXValue() >= mouseX) {
                    closestRightX = point.getXValue();
                    break;
                }
            }
            final Double closestX =
                    closestLeftX != null && closestRightX != null ?
                            min(closestLeftX, closestRightX) :
                            closestLeftX != null ? closestLeftX : closestRightX != null ? closestRightX : Double.MAX_VALUE;
            var line = closestVerticalLines
                    .stream()
                    .filter(p -> Objects.equals(p.getXValue(), closestX))
                    .toList();
            point1 = line.get(0);
            point2 = line.get(1);
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

    public void dragHandler(MouseEvent mouseEvent) {
        vesCurvesLineChart.setAnimated(false);

        Point2D pointInScene = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());

        Double mouseX = vesCurvesLineChart.getXAxis().getValueForDisplay(
                vesCurvesLineChart.getXAxis().sceneToLocal(pointInScene).getX()
        );
        Double mouseY = vesCurvesLineChart.getYAxis().getValueForDisplay(
                vesCurvesLineChart.getYAxis().sceneToLocal(pointInScene).getY()
        );

        if (point1 != null && point2 != null) {
            if (Objects.equals(point1.getXValue(), point2.getXValue())) {
                point1.setXValue(mouseX);
                point2.setXValue(mouseX);
            } else if (Objects.equals(point1.getYValue(), point2.getYValue())) {
                point1.setYValue(mouseY);
                point2.setYValue(mouseY);
            }
        }

        vesCurvesLineChart.setAnimated(true);
    }
}
