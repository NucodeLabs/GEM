package ru.nucodelabs.gem.view.control.chart

import javafx.scene.chart.ValueAxis

/**
 * Zoom support for chart, delta values for axes (not pixels)
 */
class ZoomSupport(
    val xAxis: ValueAxis<Number>,
    val yAxis: ValueAxis<Number>,
) {

    fun zoomIn(zoomDeltaForBothAxes: Double) = zoomIn(zoomDeltaForBothAxes, zoomDeltaForBothAxes)

    fun zoomIn(zoomDeltaForXAxis: Double, zoomDeltaForYAxis: Double) {
        xAxis.lowerBound += zoomDeltaForXAxis
        xAxis.upperBound -= zoomDeltaForXAxis

        yAxis.lowerBound += zoomDeltaForYAxis
        yAxis.upperBound -= zoomDeltaForYAxis
    }

    fun zoomOut(zoomDeltaForBothAxes: Double) = zoomOut(zoomDeltaForBothAxes, zoomDeltaForBothAxes)

    fun zoomOut(zoomDeltaForXAxis: Double, zoomDeltaForYAxis: Double) {
        xAxis.lowerBound -= zoomDeltaForXAxis
        xAxis.upperBound += zoomDeltaForXAxis

        yAxis.lowerBound -= zoomDeltaForYAxis
        yAxis.upperBound += zoomDeltaForYAxis
    }

}