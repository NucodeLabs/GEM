package ru.nucodelabs.gem.view.charts

import javafx.geometry.Point2D
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.input.MouseEvent

/**
 * Drag chart plot using mouse support for chart
 */
class DragViewSupport(
    val xAxis: NumberAxis,
    val yAxis: NumberAxis,
    val sceneToValueForAxis: (Point2D) -> XYChart.Data<Double, Double>,
    val sensitivity: Double = 1.0,
) {

    private lateinit var dragStart: Pair<Double, Double>

    /**
     * OnMouseDragged event
     */
    fun handleMouseDragged(mouseEvent: MouseEvent) {
        val (startX, startY) = dragStart

        val start = sceneToValueForAxis(Point2D(startX, startY))
        val now = sceneToValueForAxis(Point2D(mouseEvent.sceneX, mouseEvent.sceneY))

        val distX = (now.xValue - start.xValue) * sensitivity
        val distY = (now.yValue - start.yValue) * sensitivity

        xAxis.lowerBound -= distX
        xAxis.upperBound -= distX

        yAxis.lowerBound -= distY
        yAxis.upperBound -= distY

        dragStart = mouseEvent.sceneX to mouseEvent.sceneY
    }

    /**
     * OnMousePressed event
     */
    fun handleMousePressed(mouseEvent: MouseEvent) {
        dragStart = mouseEvent.sceneX to mouseEvent.sceneY
    }
}