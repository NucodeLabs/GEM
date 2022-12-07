package ru.nucodelabs.gem.fxmodel

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import ru.nucodelabs.geo.ves.ModelLayer
import ru.nucodelabs.gem.util.fx.getValue
import ru.nucodelabs.gem.util.fx.setValue

class ObservableModelLayer(
    power: Double,
    resistance: Double,
    isFixedPower: Boolean,
    isFixedResistance: Boolean
) {
    constructor(modelLayer: ModelLayer) : this(
        modelLayer.power,
        modelLayer.resistance,
        modelLayer.isFixedPower,
        modelLayer.isFixedResistance
    )

    fun toModelLayer() = ModelLayer(power, resistance, isFixedPower, isFixedResistance)

    private val powerProperty = SimpleDoubleProperty(power)
    fun powerProperty() = powerProperty
    var power by powerProperty

    private val resistanceProperty = SimpleDoubleProperty(resistance)
    fun resistanceProperty() = resistanceProperty
    var resistance by resistanceProperty

    private val fixedPowerProperty = SimpleBooleanProperty(isFixedPower)
    fun fixedPowerProperty() = fixedPowerProperty
    var isFixedPower by fixedPowerProperty

    private val fixedResistanceProperty = SimpleBooleanProperty(isFixedResistance)
    fun fixedResistanceProperty() = fixedResistanceProperty
    var isFixedResistance by fixedResistanceProperty
}

fun ModelLayer.toObservable() = ObservableModelLayer(this)