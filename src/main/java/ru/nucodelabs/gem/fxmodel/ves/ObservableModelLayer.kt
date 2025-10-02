package ru.nucodelabs.gem.fxmodel.ves

import javafx.beans.property.SimpleBooleanProperty
import ru.nucodelabs.geo.ves.ModelLayer
import ru.nucodelabs.geo.ves.MutableModelLayer
import ru.nucodelabs.geo.ves.ReadOnlyModelLayer
import ru.nucodelabs.kfx.observable.ConstrainedDoubleProperty
import tornadofx.getValue
import tornadofx.setValue

class ObservableModelLayer(
    power: Double,
    resistivity: Double,
    isFixedPower: Boolean,
    isFixedResistivity: Boolean
) : MutableModelLayer {
    private val powerProperty = ConstrainedDoubleProperty(power, ModelLayer::isValidPower)
    fun powerProperty() = powerProperty
    override var power by powerProperty

    private val resistivityProperty = ConstrainedDoubleProperty(resistivity, ModelLayer::isValidResistivity)
    fun resistivityProperty() = resistivityProperty
    override var resistivity by resistivityProperty

    private val fixedPowerProperty = SimpleBooleanProperty(isFixedPower)
    fun fixedPowerProperty() = fixedPowerProperty
    override var isFixedPower by fixedPowerProperty

    private val fixedResistivityProperty = SimpleBooleanProperty(isFixedResistivity)
    fun fixedResistivityProperty() = fixedResistivityProperty
    override var isFixedResistivity by fixedResistivityProperty
}

fun ReadOnlyModelLayer.toObservable() =
    ObservableModelLayer(power, resistivity, isFixedPower, isFixedResistivity)