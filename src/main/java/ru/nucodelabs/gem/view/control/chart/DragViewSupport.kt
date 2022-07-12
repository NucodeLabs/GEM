package ru.nucodelabs.gem.view.control.chart

import javafx.scene.chart.ValueAxis
import javafx.scene.input.MouseEvent

/**
 * Drag chart plot using mouse support for chart
 */
class DragViewSupport(
    val xAxis: ValueAxis<Number>,
    val yAxis: ValueAxis<Number>,
    val sensitivity: Double = 1.0,
) {

    private lateinit var dragStart: Pair<Double, Double>

    /**
     * OnMouseDragged event
     */
    fun handleMouseDragged(mouseEvent: MouseEvent) {
        val (startX, startY) = dragStart

        val axes = xAxis to yAxis
        val start = axes.valueForSceneCoordinates(startX, startY)
        val now = axes.valueForMouseCoordinates(mouseEvent)

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