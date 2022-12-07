package ru.nucodelabs.gem.view.control.chart.log

import javafx.beans.NamedArg
import ru.nucodelabs.gem.util.std.exp10
import kotlin.math.*

class PseudoLogarithmicAxis @JvmOverloads constructor(
    @NamedArg("lowerBound") lowerBound: Double = 1.0,
    @NamedArg("upperBound") upperBound: Double = 100.0
) : LogarithmicAxis(lowerBound, upperBound) {

    private val length
        get() = if (side.isVertical) height else width

    init {
//        checkBounds(lowerBound, upperBound)
    }

    private fun checkBounds(lowerBound: Double, upperBound: Double) =
        require(upperBound >= lowerBound)

    @Suppress("UNCHECKED_CAST")
    override fun calculateTickValues(length: Double, range: Any?): List<Number> {
        val ticks: MutableList<Number> = mutableListOf()

        if (range != null) {
            range as Array<Number>
            val lowerBound = range[0]
            val upperBound = range[1]
            val lowerSign = sign(lowerBound.toDouble())
            val upperSign = sign(upperBound.toDouble())
//            checkBounds(lowerBound.toDouble(), upperBound.toDouble())

            ticks += lowerBound

            val absLowerBound = abs(lowerBound.toDouble())
            val logLowerBound = if (absLowerBound < 10)
                0.0
            else
                log10(abs(lowerBound.toDouble()))
            val absUpperBound = abs(upperBound.toDouble())
            val logUpperBound = if (absUpperBound < 10)
                0.0
            else
                log10(abs(upperBound.toDouble()))

            val first = floor(logLowerBound).toInt()
            val last = floor(logUpperBound).toInt()

            if (logLowerBound != 0.0)
                for (i in 1..first) {
                    ticks += exp10(i) * lowerSign
                }

            if (lowerSign != upperSign)
                ticks += 0

            if (logUpperBound != 0.0)
                for (i in 1..last) {
                    ticks += exp10(i) * upperSign
                }

            ticks += upperBound
        }
        return ticks.distinct()
    }

    override fun calculateMinorTickMarks(): List<Number> {
        val range = range

        val majorTicks = calculateTickValues(length, range).sortedBy { it.toDouble() }.asReversed()
        val minorTicks = mutableListOf<Number>()

        for ((idx, m) in majorTicks.withIndex()) {
            if (m.toDouble() == 0.0 || m.toDouble() == 1.0 || m.toDouble() == -1.0) {
                continue
            }
            if (idx == 0 && log10(m.toDouble()) % 1.0 != 0.0) {
                val exp = log10(abs(m.toDouble())).toInt()
                val sign = sign(m.toDouble())
                val r = 10.0.pow(exp)
                for (i in 1..m.toDouble().toInt()) {
                    minorTicks += (r * i) * sign
                }
                continue
            }
            val exp = log10(abs(m.toDouble())).toInt()
            val sign = sign(m.toDouble())
            val r = 10.0.pow(exp - 1)
            for (i in 1..9) {
                if (i == 1 && r != 1.0)
                    continue
                minorTicks += (r * i) * sign
            }
            if (idx == majorTicks.lastIndex) {
                val rLast = 10.0.pow(exp)
                for (i in 1..9) {
                    if (i == 1 && r != 1.0)
                        continue
                    if (rLast * i >= majorTicks.last().toDouble()) {
                        break
                    }
                    minorTicks += rLast * i * sign
                }
            }
        }
        return minorTicks
    }

    override fun getValueForDisplay(displayPosition: Double): Number {
        val range = lowerBound - upperBound
        val percentage = if (side.isVertical)
            displayPosition / height
        else
            displayPosition / width

        return if (!inverted)
            log10(calcLinValue(range * (1.0 - percentage))) * range + upperBound
        else
            log10(calcLinValue(range * percentage)) * range + upperBound
    }

    override fun getDisplayPosition(value: Number): Double {
        val linValue = calcLinValue(value.toDouble())
        val percentage = log10(linValue)

        return if (side.isVertical) {
            if (!inverted)
                percentage * height
            else
                (1.0 - percentage) * height
        } else {
            if (!inverted)
                percentage * width
            else
                (1.0 - percentage) * width
        }
    }

    override fun getRange(): Array<Number> {
        return arrayOf<Number>(lowerBound, upperBound)
    }

    override fun autoRange(minValue: Double, maxValue: Double, length: Double, labelSize: Double): Any {
        return arrayOf<Number>(minValue, maxValue)
    }

    private fun calcLinValue(value: Double): Double {
        return (value - upperBound) / ((lowerBound - upperBound) / 9) + 1
    }
}