package ru.nucodelabs.gem.view.control.chart

import javafx.scene.chart.ValueAxis
import ru.nucodelabs.gem.extensions.std.exp10
import kotlin.math.log10

class ZoomAxis(
    val xAxis: ValueAxis<Number>,
    val yAxis: ValueAxis<Number>
) {
    fun zoom(scale: Double, position: Pair<Double, Double> = Pair(0.5, 0.5)) {
        zoomAxis(xAxis, scale, position.first)
        zoomAxis(yAxis, scale, position.second)
    }

    private fun zoomAxis(axis: ValueAxis<Number>, scale: Double, position: Double) {
        if (scale < 0 || position < 0 || position > 1)
            throw Exception("zoomAxis wrong data")

        val lowerBound = axis.lowerBound
        val upperBound = axis.upperBound

        val positionValue = axis.getValueForDisplay(axis.length * position) as Double

        axis.lowerBound = exp10(((log10(positionValue) - log10(lowerBound)) * (1.0 - 1.0 / scale)) + log10(lowerBound))
        axis.upperBound = exp10(log10(upperBound) - ((log10(upperBound) - log10(positionValue)) * (1.0 - 1.0 / scale)))
    }
}

