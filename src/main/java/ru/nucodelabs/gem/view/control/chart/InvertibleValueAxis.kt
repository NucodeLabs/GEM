package ru.nucodelabs.gem.view.control.chart

import javafx.beans.property.SimpleBooleanProperty
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
}