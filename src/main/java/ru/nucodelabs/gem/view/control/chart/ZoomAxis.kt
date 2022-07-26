package ru.nucodelabs.gem.view.control.chart

import javafx.scene.chart.ValueAxis
import ru.nucodelabs.gem.extensions.std.exp10
import kotlin.math.log10

class ZoomAxis(
    val xAxis: ValueAxis<Number>,
    val yAxis: ValueAxis<Number>
) {
    fun zoom() {

    }

    fun zoomAxis(axis: ValueAxis<Number>, scale: Double, position: Double, isVertical: Boolean) {
        if (scale < 0 || position < 0 || position > 1)
            throw Exception("zoomAxis wrong data")

        val lowerBound = axis.lowerBound
        val upperBound = axis.upperBound

        val range = upperBound - lowerBound
        val positionValue = if (isVertical)
            axis.getValueForDisplay(axis.height * position) as Double
        else
            axis.getValueForDisplay(axis.width * position) as Double

        axis.lowerBound = log10((exp10(positionValue) - exp10(lowerBound)) * scale)
    }
}