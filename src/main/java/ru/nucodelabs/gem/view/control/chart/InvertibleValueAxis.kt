package ru.nucodelabs.gem.view.control.chart

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.chart.ValueAxis

abstract class InvertibleValueAxis<N : Number>(
    lowerBound: Double,
    upperBound: Double
) : ValueAxis<N>(lowerBound, upperBound) {
    private val _inverted = SimpleBooleanProperty(false)
    var inverted
        get() = _inverted.get()
        set(value) = _inverted.set(value)

    fun invertedProperty() = _inverted
}