package ru.nucodelabs.gem.view.control.chart.log

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.NamedArg
import javafx.beans.binding.Bindings.createDoubleBinding
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.util.Duration
import ru.nucodelabs.gem.view.control.chart.InvertibleValueAxis
import ru.nucodelabs.util.std.exp10
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

open class LogarithmicAxis @JvmOverloads constructor(
    @NamedArg("lowerBound") lowerBound: Double = 1.0,
    @NamedArg("upperBound") upperBound: Double = 100.0
) : InvertibleValueAxis<Number>(lowerBound, upperBound) {
    private val lowerRangeTimeline = Timeline()
    private val upperRangeTimeline = Timeline()
    private val logUpperBound: DoubleProperty = SimpleDoubleProperty()
    private val logLowerBound: DoubleProperty = SimpleDoubleProperty()

    private val length
        get() = if (side.isVertical) height else width

    init {
        animated = false
        isAutoRanging = true
        //checkBounds(lowerBound, upperBound)
        bindLogBoundsToDefaultBounds()
    }

    private fun bindLogBoundsToDefaultBounds() {
        logLowerBound.bind(
            createDoubleBinding(
                { log10(lowerBound) },
                lowerBoundProperty()
            )
        )
        logUpperBound.bind(
            createDoubleBinding(
                { log10(upperBound) },
                upperBoundProperty()
            )
        )
    }

    override fun layoutChildren() {
        super.layoutChildren()
        if (isTickLabelsVisible) {
            checkOverlaps()
        }
    }

    private fun checkOverlaps() {
        if (tickMarks.size <= 1) {
            return
        }

        val tick1 = tickMarks[0].apply { isTextVisible = true }
        val tick2 = tickMarks[1].apply { isTextVisible = true }
        if (!inverted) {
            if (isTickLabelsOverlap(side, tick1, tick2, tickLabelGap)) {
                tick1.isTextVisible = false
            }
        } else {
            if (isTickLabelsOverlap(side, tick2, tick1, tickLabelGap)) {
                tick1.isTextVisible = false
            }
        }

        val tickPreLast = tickMarks[tickMarks.lastIndex - 1].apply { isTextVisible = true }
        val tickLast = tickMarks[tickMarks.lastIndex].apply { isTextVisible = true }
        if (!inverted) {
            if (isTickLabelsOverlap(side, tickPreLast, tickLast, tickLabelGap)) {
                tickLast.isTextVisible = false
            }
        } else {
            if (isTickLabelsOverlap(side, tickLast, tickPreLast, tickLabelGap)) {
                tickLast.isTextVisible = false
            }
        }

        tickMarksTextNodes.forEach { (mark, node) -> node.isVisible = mark.isTextVisible }
    }

    private fun checkBounds(lowerBound: Double, upperBound: Double) =
        require(lowerBound > 0 && upperBound > 0 && lowerBound <= upperBound) { "Lower: $lowerBound; Upper: $upperBound" }


    override fun calculateMinorTickMarks(): List<Number> {
        val range = range

        val majorTicks = calculateTickValues(length, range)
        val minorTicks = mutableListOf<Number>()

        for ((idx, m) in majorTicks.withIndex()) {
            val exp = log10(m.toDouble()).toInt()
            val r = 10.0.pow(exp - 1)
            for (i in 2..9) {
                minorTicks += (r * i)
            }
            if (idx == majorTicks.lastIndex) {
                val rLast = 10.0.pow(exp)
                for (i in 2..9) {
                    if (rLast * i >= majorTicks.last().toDouble()) {
                        break
                    }
                    minorTicks += rLast * i
                }
            }
        }

        return minorTicks
    }

    @Suppress("UNCHECKED_CAST")
    override fun calculateTickValues(length: Double, range: Any?): List<Number> {
        val ticks: MutableList<Number> = mutableListOf()

        if (range != null) {
            // Number lowerBound = ((Number[]) range)[0];
            range as Array<Number>
            val lowerBound = range[0]
            val upperBound = range[1]
            //checkBounds(lowerBound.toDouble(), upperBound.toDouble())

            ticks += lowerBound

            val logLowerBound = log10(lowerBound.toDouble())
            val logUpperBound = log10(upperBound.toDouble())
            val first = ceil(logLowerBound).toInt()
            val last = floor(logUpperBound).toInt()

            for (i in first..last) {
                ticks += exp10(i)
            }

            ticks += upperBound
        }
        return ticks.distinct()
    }

    override fun getRange(): Array<Number> {
        return arrayOf(lowerBound.coerceAtLeast(Double.MIN_VALUE), upperBound.coerceAtLeast(Double.MIN_VALUE))
    }

    override fun getTickMarkLabel(value: Number): String = tickLabelFormatter?.toString(value) ?: value.toString()

    @Suppress("UNCHECKED_CAST")
    override fun setRange(range: Any?, animate: Boolean) {
        if (range != null) {
            range as Array<Number>
            val lowerBound = range[0]
            val upperBound = range[1]
            //checkBounds(lowerBound.toDouble(), upperBound.toDouble())

            if (animate) {
                try {
                    lowerRangeTimeline.keyFrames.clear()
                    upperRangeTimeline.keyFrames.clear()
                    lowerRangeTimeline.keyFrames.addAll(
                        KeyFrame(
                            Duration.ZERO,
                            KeyValue(lowerBoundProperty(), lowerBoundProperty().get())
                        ),
                        KeyFrame(
                            Duration(ANIMATION_TIME),
                            KeyValue(lowerBoundProperty(), lowerBound.toDouble())
                        )
                    )
                    upperRangeTimeline.keyFrames.addAll(
                        KeyFrame(
                            Duration.ZERO,
                            KeyValue(upperBoundProperty(), upperBoundProperty().get())
                        ),
                        KeyFrame(
                            Duration(ANIMATION_TIME),
                            KeyValue(upperBoundProperty(), upperBound.toDouble())
                        )
                    )
                    lowerRangeTimeline.play()
                    upperRangeTimeline.play()
                } catch (e: Exception) {
                    lowerBoundProperty().set(lowerBound.toDouble())
                    upperBoundProperty().set(upperBound.toDouble())
                }
            }
            lowerBoundProperty().set(lowerBound.toDouble())
            upperBoundProperty().set(upperBound.toDouble())
        }
    }

    override fun getValueForDisplay(displayPosition: Double): Number {
        val delta = logUpperBound.get() - logLowerBound.get()

        return if (side.isVertical) {
            if (!inverted) {
                (10.0.pow(((1.0 - displayPosition / height) * delta) + logLowerBound.get()))
            } else {
                (10.0.pow(((displayPosition / height) * delta) + logLowerBound.get()))
            }
        } else {
            if (!inverted) {
                10.0.pow(((displayPosition / width) * delta) + logLowerBound.get())
            } else {
                10.0.pow(((1.0 - displayPosition / width) * delta) + logLowerBound.get())
            }
        }
    }

    override fun getDisplayPosition(value: Number): Double {
        val delta = logUpperBound.get() - logLowerBound.get()
        val deltaV = log10(value.toDouble()) - logLowerBound.get()

        return if (side.isVertical) {
            if (!inverted) {
                (1.0 - deltaV / delta) * height
            } else {
                (deltaV / delta) * height
            }
        } else {
            if (!inverted) {
                (deltaV / delta) * width
            } else {
                (1.0 - deltaV / delta) * width
            }
        }
    }

    override fun autoRange(minValue: Double, maxValue: Double, length: Double, labelSize: Double): Any {
        return arrayOf<Number>(minValue.coerceAtLeast(Double.MIN_VALUE), maxValue.coerceAtLeast(Double.MIN_VALUE))
    }


    companion object {
        private const val ANIMATION_TIME = 700.0
    }
}