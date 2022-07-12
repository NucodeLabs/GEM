package ru.nucodelabs.gem.view.control.chart

import javafx.beans.NamedArg
import javafx.beans.property.SimpleDoubleProperty
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

class NucodeNumberAxis @JvmOverloads constructor(
    @NamedArg("lowerBound") lowerBound: Double = 0.0,
    @NamedArg("upperBound") upperBound: Double = 100.0
) : InvertibleValueAxis<Number>(lowerBound, upperBound) {

    init {
        // bugfix: autoRanging is true by default, but it won't work until you set it manually
        isAutoRanging = true
    }

    private val _tickUnit = SimpleDoubleProperty(10.0)
    fun tickUnitProperty() = _tickUnit
    var tickUnit
        get() = _tickUnit.get()
        set(value) = _tickUnit.set(value)

    override fun getDisplayPosition(value: Number): Double {
        val interval = upperBound - lowerBound
        val intervalToVal = value.toDouble() - lowerBound
        val proportion = intervalToVal / interval
        return when {
            side.isVertical -> if (!inverted) {
                height - height * proportion
            } else {
                height * proportion
            }
            side.isHorizontal -> if (!inverted) {
                width * proportion
            } else {
                width - width * proportion
            }
            else -> throw IllegalStateException("что-то пошло не так")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun setRange(range: Any?, animate: Boolean) {
        range as List<Double>
        lowerBound = range[0]
        upperBound = range[1]
    }

    override fun getRange(): List<Double> = listOf(lowerBound, upperBound, tickUnit)

    override fun autoRange(
        minValue: Double,
        maxValue: Double,
        length: Double,
        labelSize: Double
    ): List<Double> = listOf(minValue, maxValue, tickUnit)

    @Suppress("UNCHECKED_CAST")
    override fun calculateTickValues(length: Double, range: Any?): List<Number> {
        range as List<Double>
        val lowerBound = range[0]
        val upperBound = range[1]
        val tickUnit = range[2]
        val ticks = mutableListOf<Number>()

        when {
            lowerBound == upperBound -> ticks += upperBound
            tickUnit <= 0 -> {
                ticks += lowerBound
                ticks += upperBound
            }
            tickUnit > 0 -> {
                ticks += lowerBound
                if ((upperBound - lowerBound) / tickUnit > 2000) {
                    // This is a ridiculous amount of major tick marks, something has probably gone wrong
                    System.err.println(
                        "Warning we tried to create more than 2000 major tick marks on $this. " +
                                "Lower Bound=$lowerBound, " +
                                "Upper Bound=$upperBound, " +
                                "Tick Unit=$tickUnit"
                    )
                } else {
                    if (lowerBound + tickUnit < upperBound) {
                        // If tickUnit is integer, start with the nearest integer
                        var major = if (round(tickUnit) == tickUnit) {
                            ceil(lowerBound)
                        } else {
                            lowerBound + tickUnit
                        }

                        val count = ceil((upperBound - major) / tickUnit).toInt()

                        for (i in 1..count) {
                            if (major >= upperBound) {
                                break
                            }
                            if (major !in ticks) {
                                ticks += major
                            }
                            major += tickUnit
                        }
                    }
                }
                ticks += upperBound
            }
        }
        return ticks
    }

    // How to extend final class?
    // - Copy/Paste Pattern

    override fun calculateMinorTickMarks(): List<Number> {
        val minorTicks = mutableListOf<Number>()
        val lowerBound = lowerBound
        val upperBound = upperBound
        val tickUnit = tickUnit

        val minorUnit = tickUnit / minorTickCount.coerceAtLeast(1)
        if (tickUnit > 0) {
            if ((upperBound - lowerBound) / minorUnit > 10_000) {
                System.err.println(
                    "Warning we tried to create more than 10000 minor tick marks on $this. " +
                            "Lower Bound=${getLowerBound()}, " +
                            "Upper Bound=${getUpperBound()}, " +
                            "Tick Unit=$tickUnit"
                )
                return minorTicks
            }
            val tickUnitIsInteger = round(tickUnit) == tickUnit
            if (tickUnitIsInteger) {
                var minor = floor(lowerBound) + minorUnit
                val count = ceil((ceil(lowerBound) - minor) / minorUnit).toInt()
                var i = 0
                while (minor < ceil(lowerBound) && i < count) {
                    if (minor > lowerBound) {
                        minorTicks.add(minor)
                    }
                    minor += minorUnit
                    i++
                }
            }
            var major = if (tickUnitIsInteger) ceil(lowerBound) else lowerBound
            val count = ceil((upperBound - major) / tickUnit).toInt()
            var i = 0
            while (major < upperBound && i < count) {
                val next = (major + tickUnit).coerceAtMost(upperBound)
                var minor = major + minorUnit
                val minorCount = ceil((next - minor) / minorUnit).toInt()
                var j = 0
                while (minor < next && j < minorCount) {
                    minorTicks.add(minor)
                    minor += minorUnit
                    j++
                }
                major += tickUnit
                i++
            }
        }
        return minorTicks
    }

    override fun getTickMarkLabel(value: Number?): String = tickLabelFormatter?.toString(value) ?: value.toString()
}