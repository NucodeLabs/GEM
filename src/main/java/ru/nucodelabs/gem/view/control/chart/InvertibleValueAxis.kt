package ru.nucodelabs.gem.view.control.chart

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Side
import javafx.scene.chart.ValueAxis
import javafx.scene.text.Text

abstract class InvertibleValueAxis<N : Number>(
    lowerBound: Double,
    upperBound: Double
) : ValueAxis<N>(lowerBound, upperBound) {
    private val _inverted = SimpleBooleanProperty(false)
    var inverted
        get() = _inverted.get()
        set(value) = _inverted.set(value)

    fun invertedProperty() = _inverted

    val tickMarksTextNodes: Map<TickMark<N>, Text>
        get() = buildMap {
            val textNodes = childrenTextNodes
            tickMarks.forEach { tick ->
                textNodes.find { node -> node.text == getTickMarkLabel(tick.value) }?.let { node ->
                    put(tick, node)
                }
            }
        }

    protected fun isTickLabelsOverlap(side: Side, m1: TickMark<N>, m2: TickMark<N>, gap: Double): Boolean {
        if (!m1.isTextVisible || !m2.isTextVisible) return false
        val m1Size: Double = measureTickMarkSize(m1.value, side)
        val m2Size: Double = measureTickMarkSize(m2.value, side)
        val m1Start = m1.position - m1Size / 2
        val m1End = m1.position + m1Size / 2
        val m2Start = m2.position - m2Size / 2
        val m2End = m2.position + m2Size / 2
        return if (side.isVertical) m1Start - m2End <= gap else m2Start - m1End <= gap
    }

    private fun measureTickMarkSize(value: N, side: Side): Double {
        val size = measureTickMarkSize(value, tickLabelRotation)
        return if (side.isVertical) size.height else size.width
    }
}