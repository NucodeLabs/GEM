package ru.nucodelabs.gem.view.control.chart.log

import javafx.scene.chart.ValueAxis
import ru.nucodelabs.kfx.ext.length
import ru.nucodelabs.util.std.exp10
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min

class LogarithmicChartNavigationSupport(
    val xAxis: LogarithmicAxis,
    val yAxis: LogarithmicAxis,
    val maxRange: Double = 100_000_000.0,
    val minRange: Double = 1.0
) {
    fun zoom(scale: Double, position: Pair<Double, Double> = Pair(0.5, 0.5)) {
        if (scale < 0 || position.first < 0 || position.first > 1 || position.second < 0 || position.second > 1)
            return

        val minRange = min(xAxis.upperBound - xAxis.lowerBound, yAxis.upperBound - yAxis.lowerBound)
        val maxRange = max(xAxis.upperBound - xAxis.lowerBound, yAxis.upperBound - yAxis.lowerBound)
        if ((minRange < this.minRange && scale > 1.0) || (maxRange > this.maxRange && scale < 1.0))
            return

        zoomAxis(xAxis, scale, position.first)
        zoomAxis(yAxis, scale, position.second)
    }

    fun drag(deltaCoords: Pair<Double, Double>) {
        val xRange = xAxis.upperBound - xAxis.lowerBound
        val yRange = yAxis.upperBound - yAxis.lowerBound
        val dX = deltaCoords.first
        val dY = deltaCoords.second

        if (dX < 0 && xRange < minRange || dX > 0 && xRange > maxRange
            || dY < 0 && yRange < minRange || dY > 0 && yRange > maxRange
        )
            return

        dragAxis(xAxis, dX)
        dragAxis(yAxis, dY)
    }

    private fun dragAxis(axis: ValueAxis<Number>, deltaPos: Double) {
        val lowerBound = axis.lowerBound
        val upperBound = axis.upperBound

        axis.lowerBound = exp10(((log10(upperBound) - log10(lowerBound)) * deltaPos) + log10(lowerBound))
        axis.upperBound = exp10(((log10(upperBound) - log10(lowerBound)) * deltaPos) + log10(upperBound))
    }

    private fun zoomAxis(axis: ValueAxis<Number>, scale: Double, position: Double) {
        val lowerBound = axis.lowerBound
        val upperBound = axis.upperBound

        val positionValue = axis.getValueForDisplay(axis.length * position) as Double

        axis.lowerBound = exp10(((log10(positionValue) - log10(lowerBound)) * (1.0 - 1.0 / scale)) + log10(lowerBound))
        axis.upperBound = exp10(log10(upperBound) - ((log10(upperBound) - log10(positionValue)) * (1.0 - 1.0 / scale)))
    }
}

