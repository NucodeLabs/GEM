package ru.nucodelabs.gem.fxmodel.anisotropy

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import tornadofx.getValue
import tornadofx.setValue

class ObservableSignal(
    ab2: Double,
    mn2: Double,
    amperage: Double,
    voltage: Double,
    resistivityApparent: Double,
    errorResistivityApparent: Double,
    isHidden: Boolean
) {
    private val ab2Property = SimpleDoubleProperty(ab2)
    fun ab2Property() = ab2Property
    var ab2 by ab2Property

    private val mn2Property = SimpleDoubleProperty(mn2)
    fun mn2Property() = mn2Property
    var mn2 by mn2Property

    private val amperageProperty = SimpleDoubleProperty(amperage)
    fun amperageProperty() = amperageProperty
    var amperage by amperageProperty

    private val voltageProperty = SimpleDoubleProperty(voltage)
    fun voltageProperty() = voltageProperty
    var voltage by voltageProperty

    private val resistivityApparentProperty = SimpleDoubleProperty(resistivityApparent)
    fun resistivityApparentProperty() = resistivityApparentProperty
    var resistivityApparent by resistivityApparentProperty

    private val errorResistivityApparentProperty = SimpleDoubleProperty(errorResistivityApparent)
    fun errorResistivityApparentProperty() = errorResistivityApparentProperty
    var errorResistivityApparent by errorResistivityApparentProperty

    private val hiddenProperty = SimpleBooleanProperty(isHidden)
    fun hiddenProperty() = hiddenProperty
    var isHidden by hiddenProperty
}