package ru.nucodelabs.gem.view.control.chart

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.NamedArg
import javafx.beans.binding.Bindings.createDoubleBinding
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.chart.ValueAxis
import javafx.util.Duration
import kotlin.math.log10
import kotlin.math.pow

class LogarithmicAxis @JvmOverloads constructor(
    @NamedArg("lowerBound") lowerBound: Double = 0.0,
    @NamedArg("upperBound") upperBound: Double = 100.0
) : ValueAxis<Number>(lowerBound, upperBound) {
    private val lowerRangeTimeline = Timeline()
    private val upperRangeTimeline = Timeline()
    private val logUpperBound: DoubleProperty = SimpleDoubleProperty()
    private val logLowerBound: DoubleProperty = SimpleDoubleProperty()

    init {
        validateBounds(lowerBound, upperBound)
        bindLogBoundsToDefaultBounds()
    }

    /**
     * Bind our logarithmic bounds with the super class bounds, consider the base 10 logarithmic scale.
     */
    private fun bindLogBoundsToDefaultBounds() {
        logLowerBound.bind(
            createDoubleBinding(
                { log10(lowerBoundProperty().get()) },
                lowerBoundProperty()
            )
        )
        logUpperBound.bind(
            createDoubleBinding(
                { log10(upperBoundProperty().get()) },
                upperBoundProperty()
            )
        )
    }

    /**
     * Validate the bounds by throwing an exception if the values are not conform to the mathematics log interval:
     * [0,Double.MAX_VALUE]
     */
    private fun validateBounds(lowerBound: Double, upperBound: Double) =
        require(lowerBound < 0 || upperBound < 0 || lowerBound <= upperBound)


    override fun calculateMinorTickMarks(): List<Number> {
        val range = range
        val minorTickMarksPositions: MutableList<Number> = mutableListOf()
        val upperBound = range[1]
        val logUpperBound = log10(upperBound.toDouble())

        var i = 0.0
        while (i <= logUpperBound) {
            var j = 0.0
            while (j <= 9) {
                val value = j * 10.0.pow(i)
                minorTickMarksPositions += value
                j += 1.0 / minorTickCount
            }
            i += 1.0
        }

        return minorTickMarksPositions
    }

    @Suppress("UNCHECKED_CAST")
    override fun calculateTickValues(length: Double, range: Any?): List<Number> {
        val tickPositions: MutableList<Number> = mutableListOf()
        if (range != null) {
            // Number lowerBound = ((Number[]) range)[0];
            range as Array<Number>
            val upperBound = range[1]
            // double logLowerBound = Math.log10(lowerBound.doubleValue());
            val logUpperBound = log10(upperBound.toDouble())
            var i = 0.0
            while (i <= logUpperBound) {
                for (j in 1..9) {
                    val value = j * 10.0.pow(i)
                    tickPositions.add(value)
                }
                i += 1.0
            }
        }
        return tickPositions
    }

    override fun getRange(): Array<Number> = arrayOf(lowerBoundProperty().get(), upperBoundProperty().get())

    override fun getTickMarkLabel(value: Number): String = tickLabelFormatter?.toString(value) ?: value.toString()

    @Suppress("UNCHECKED_CAST")
    override fun setRange(range: Any?, animate: Boolean) {
        if (range != null) {
            range as Array<Number>
            val lowerBound = range[0]
            val upperBound = range[1]
            validateBounds(lowerBound.toDouble(), upperBound.toDouble())

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
            10.0.pow(((1.0 - displayPosition / height) * delta) + logLowerBound.get())
        } else {
            10.0.pow(((displayPosition / width) * delta) + logLowerBound.get())
        }
    }

    override fun getDisplayPosition(value: Number): Double {
        val delta = logUpperBound.get() - logLowerBound.get()
        val deltaV = log10(value.toDouble()) - logLowerBound.get()

        return if (side.isVertical) {
            (1.0 - deltaV / delta) * height
        } else {
            (deltaV / delta) * width
        }
    }

    override fun autoRange(minValue: Double, maxValue: Double, length: Double, labelSize: Double): Any =
        arrayOf<Number>(lowerBoundProperty().get(), upperBoundProperty().get())


    companion object {
        /**
         * The time of animation in ms
         */
        private const val ANIMATION_TIME = 700.0
    }
}