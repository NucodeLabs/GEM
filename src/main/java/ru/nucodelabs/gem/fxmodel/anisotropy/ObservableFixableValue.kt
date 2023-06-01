package ru.nucodelabs.gem.fxmodel.anisotropy

import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import ru.nucodelabs.gem.util.fx.getValue
import ru.nucodelabs.gem.util.fx.setValue

class ObservableFixableValue<T>(
    value: T,
    isFixed: Boolean
) {
    private val valueProperty: ObjectProperty<T> = SimpleObjectProperty(value)
    var value: T by valueProperty
    fun valueProperty() = valueProperty

    private val isFixedProperty: BooleanProperty = SimpleBooleanProperty(isFixed)
    fun fixedProperty() = isFixedProperty
    var isFixed by isFixedProperty
}