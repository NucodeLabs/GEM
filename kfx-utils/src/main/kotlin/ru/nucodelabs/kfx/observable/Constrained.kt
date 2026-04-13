package ru.nucodelabs.kfx.observable

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty


class ConstrainedObjectProperty<T>(
    value: T?,
    val isValid: (T?) -> Boolean
) : SimpleObjectProperty<T>(value) {

    override fun set(value: T?) {
        if (isValid(value)) super.set(value)
    }
}

class ConstrainedDoubleProperty(
    value: Double,
    val isValid: (Double) -> Boolean
) : SimpleDoubleProperty(value) {

    override fun set(value: Double) {
        if (isValid(value)) super.set(value)
    }
}