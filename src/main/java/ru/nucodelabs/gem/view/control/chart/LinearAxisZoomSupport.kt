package ru.nucodelabs.gem.view.control.chart

import javafx.scene.chart.ValueAxis

interface Zoomer {
    /**
     * If only scale for X passed then it used for both axes.
     * If `out` is `false` then zoom in performs
     */
    fun zoom(scaleForXAxis: Double, scaleForYAxis: Double = scaleForXAxis, out: Boolean)
}

/**
 * Zoom support for chart, delta values for axes (not pixels)
 */
class LinearAxisZoomSupport(
    val xAxis: ValueAxis<Number>,
    val yAxis: ValueAxis<Number>,
) : Zoomer {
    /**
     * If only scale for X passed then it used for both axes.
     * If `out` is `false` then zoom in performs
     */
    override fun zoom(scaleForXAxis: Double, scaleForYAxis: Double, out: Boolean) {
        infix fun Double.operation(other: Double) =
            if (out) {
                this - other
            } else {
                this + other
            }

        xAxis.lowerBound = xAxis.lowerBound operation xAxis.lowerBound / scaleForXAxis
        xAxis.upperBound = xAxis.upperBound operation -xAxis.upperBound / scaleForXAxis

        yAxis.lowerBound = yAxis.lowerBound operation yAxis.lowerBound / scaleForYAxis
        yAxis.upperBound = yAxis.upperBound operation -yAxis.upperBound / scaleForYAxis
    }
}