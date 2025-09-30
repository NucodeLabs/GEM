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
    resistance: Double,
    isFixedPower: Boolean,
    isFixedResistance: Boolean
) : MutableModelLayer {
    private val powerProperty = ConstrainedDoubleProperty(power, ModelLayer::isValidPower)
    fun powerProperty() = powerProperty
    override var power by powerProperty

    private val resistanceProperty = ConstrainedDoubleProperty(resistance, ModelLayer::isValidResist)
    fun resistanceProperty() = resistanceProperty
    override var resistance by resistanceProperty

    private val fixedPowerProperty = SimpleBooleanProperty(isFixedPower)
    fun fixedPowerProperty() = fixedPowerProperty
    override var isFixedPower by fixedPowerProperty

    private val fixedResistanceProperty = SimpleBooleanProperty(isFixedResistance)
    fun fixedResistanceProperty() = fixedResistanceProperty
    override var isFixedResistance by fixedResistanceProperty
}

fun ReadOnlyModelLayer.toObservable() =
    ObservableModelLayer(power, resistance, isFixedPower, isFixedResistance)