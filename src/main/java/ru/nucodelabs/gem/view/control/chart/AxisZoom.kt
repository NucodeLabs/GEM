package ru.nucodelabs.gem.view.control.chart

import javafx.scene.chart.ValueAxis

class AxisZoom(
    val xAxis: ValueAxis<Number>,
    val yAxis: ValueAxis<Number>
) {
    fun zoom(scaleForXAxis: Double, scaleForYAxis: Double = scaleForXAxis, out: Boolean = false) {
        infix fun Double.operation(other: Double) =
            if (out) {
                this - other
            } else {
                this + other
            }

        var offsetLower = offsetForLowerBound(xAxis, scaleForXAxis, out)
        var offsetUpper = offsetForUpperBound(xAxis, scaleForXAxis, out)

        xAxis.lowerBound = xAxis.lowerBound operation offsetLower
        xAxis.upperBound = xAxis.upperBound operation -offsetUpper

        offsetLower = offsetForLowerBound(yAxis, scaleForYAxis, out)
        offsetUpper = offsetForUpperBound(yAxis, scaleForYAxis, out)

        yAxis.lowerBound = yAxis.lowerBound operation offsetLower
        yAxis.upperBound = yAxis.upperBound operation -offsetUpper
    }

    private fun offsetForUpperBound(axis: ValueAxis<Number>, scale: Double, out: Boolean): Double {
        if (scale == 1.0) {
            return 0.0
        }
        val s = if (out) (1.0 / scale) else scale
        return if (axis.side.isHorizontal) {
            val totalOffset = axis.width * (s - 1) / 2
            val offset = totalOffset / 2
            if (!out) {
                axis.getValueForDisplay(axis.width + offset).toDouble() - axis.upperBound
            } else {
                axis.upperBound - axis.getValueForDisplay(axis.width - offset).toDouble()
            }
        } else {
            val totalOffset = axis.height * (s - 1)
            val offset = totalOffset / 2
            if (!out) {
                axis.getValueForDisplay(-offset).toDouble() - axis.upperBound
            } else {
                axis.upperBound - axis.getValueForDisplay(+offset).toDouble()
            }
        }
    }

    private fun offsetForLowerBound(axis: ValueAxis<Number>, scale: Double, out: Boolean): Double {
        if (scale == 1.0) {
            return 0.0
        }
        val s = if (out) (1.0 / scale) else scale
        return if (axis.side.isHorizontal) {
            val totalOffset = axis.width * (s - 1) / 2
            val offset = totalOffset / 2
            if (!out) {
                axis.lowerBound - axis.getValueForDisplay(-offset).toDouble()
            } else {
                axis.getValueForDisplay(+offset).toDouble() - axis.lowerBound
            }
        } else {
            val totalOffset = axis.height * (s - 1) / 2
            val offset = totalOffset / 2
            if (!out) {
                axis.lowerBound - axis.getValueForDisplay(axis.height + offset).toDouble()
            } else {
                axis.getValueForDisplay(axis.height - offset).toDouble() - axis.lowerBound
            }
        }
    }
}