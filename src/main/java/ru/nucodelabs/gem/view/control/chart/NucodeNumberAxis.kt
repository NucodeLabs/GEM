package ru.nucodelabs.gem.view.control.chart

import javafx.beans.InvalidationListener
import javafx.beans.NamedArg
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.collections.ListChangeListener
import javafx.geometry.Side
import javafx.util.StringConverter
import ru.nucodelabs.gem.extensions.fx.getValue
import ru.nucodelabs.gem.extensions.fx.observableListOf
import ru.nucodelabs.gem.extensions.fx.setValue
import kotlin.math.ceil
import kotlin.math.floor

class NucodeNumberAxis @JvmOverloads constructor(
    @NamedArg("lowerBound") lowerBound: Double = 0.0,
    @NamedArg("upperBound") upperBound: Double = 100.0
) : InvertibleValueAxis<Number>(lowerBound, upperBound) {

    private val forceMarksOnlyProperty = SimpleBooleanProperty(false)
    fun forceMarksOnlyProperty() = forceMarksOnlyProperty
    var isForceMarksOnly by forceMarksOnlyProperty

    val forceMarks = observableListOf<Double>()

    init {
        forceMarks.addListener(ListChangeListener { c ->
            while (c.next()) {
                tickMarksUpdated()
            }
        })
    }

    private val tickUnitProperty = SimpleDoubleProperty(10.0)
    fun tickUnitProperty() = tickUnitProperty
    var tickUnit by tickUnitProperty

    init {
        tickUnitProperty().addListener(InvalidationListener {
            if (!isAutoRanging) {
                invalidateRange()
                requestAxisLayout()
            }
        })
    }

    class DefaultConverter : StringConverter<Number>() {
        override fun toString(obj: Number?): String = obj.toString()

        override fun fromString(string: String?): Number = string?.toDouble() ?: 0.0
    }

    init {
        animated = false
        tickLabelFormatter = DefaultConverter()
    }

    override fun layoutChildren() {
        super.layoutChildren()
        checkOverlaps()
        scale = calculateNewScale(
            if (side.isHorizontal) width else height,
            lowerBound,
            upperBound
        )
    }

    private fun checkOverlaps() {
        if (tickMarks.size <= 1) {
            return
        }

        val tick1 = tickMarks[0].apply { isTextVisible = true }
        val tick2 = tickMarks[1].apply { isTextVisible = true }
        if (!inverted) {
            if (isTickLabelsOverlap(side, tick1, tick2, tickLabelGap)) {
                tick2.isTextVisible = false
            }
        } else {
            if (isTickLabelsOverlap(side, tick2, tick1, tickLabelGap)) {
                tick2.isTextVisible = false
            }
        }

        val tickPreLast = tickMarks[tickMarks.lastIndex - 1].apply { isTextVisible = true }
        val tickLast = tickMarks[tickMarks.lastIndex].apply { isTextVisible = true }
        if (!inverted) {
            if (isTickLabelsOverlap(side, tickPreLast, tickLast, tickLabelGap)) {
                tickPreLast.isTextVisible = false
            }
        } else {
            if (isTickLabelsOverlap(side, tickLast, tickPreLast, tickLabelGap)) {
                tickPreLast.isTextVisible = false
            }
        }

        tickMarksTextNodes.forEach { (mark, node) -> node.isVisible = mark.isTextVisible }
    }

    private fun isTickLabelsOverlap(side: Side, m1: TickMark<Number>, m2: TickMark<Number>, gap: Double): Boolean {
        if (!m1.isTextVisible || !m2.isTextVisible) return false
        val m1Size: Double = measureTickMarkSize(m1.value, side)
        val m2Size: Double = measureTickMarkSize(m2.value, side)
        val m1Start = m1.position - m1Size / 2
        val m1End = m1.position + m1Size / 2
        val m2Start = m2.position - m2Size / 2
        val m2End = m2.position + m2Size / 2
        return if (side.isVertical) m1Start - m2End <= gap else m2Start - m1End <= gap
    }

    private fun measureTickMarkSize(value: Number, side: Side): Double {
        val size = measureTickMarkSize(value, tickLabelRotation)
        return if (side.isVertical) size.height else size.width
    }

    override fun getDisplayPosition(value: Number): Double {
        val intervalToVal = value.toDouble() - lowerBound
        val res = intervalToVal * scale
        // scale is negative if vertical axis
        return when {
            side.isVertical -> if (!inverted) height + res else -res
            side.isHorizontal -> if (!inverted) res else width - res
            else -> throw IllegalStateException("что-то пошло не так")
        }
    }

    override fun getValueForDisplay(displayPosition: Double): Number {
        return when {
            side.isVertical -> if (!inverted) (displayPosition - height / scale) + lowerBound else (-displayPosition / scale) + lowerBound
            side.isHorizontal -> if (!inverted) (displayPosition / scale) + lowerBound else ((width - displayPosition) / scale + lowerBound)
            else -> throw IllegalStateException()
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
        ticks += forceMarks

        if (isForceMarksOnly) {
            return ticks
        }

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
                        var major = if (tickUnit % 1 == 0.0) lowerBound + tickUnit else ceil(lowerBound)
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
            val tickUnitIsInteger = tickUnit % 1 == 0.0
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
        return if (!inverted) minorTicks else minorTicks.reversed()
    }

    override fun getTickMarkLabel(value: Number?): String = tickLabelFormatter?.toString(value) ?: value.toString()
}