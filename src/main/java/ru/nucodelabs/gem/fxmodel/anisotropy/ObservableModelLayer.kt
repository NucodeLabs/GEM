package ru.nucodelabs.gem.fxmodel.anisotropy

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import ru.nucodelabs.gem.util.fx.getValue
import ru.nucodelabs.gem.util.fx.setValue

class ObservableModelLayer(
    power: Double,
    resistance: Double,
    isFixedPower: Boolean,
    isFixedResistance: Boolean,
    verticalAnisotropyCoefficient: Double,
    azimuth: Double,
    azimuthAnisotropyCoefficient: Double,
) {
    private val powerProperty: DoubleProperty = SimpleDoubleProperty(power)
    var power by powerProperty
    fun powerProperty() = powerProperty

    private val resistanceProperty: DoubleProperty = SimpleDoubleProperty(resistance)
    var resistance by resistanceProperty
    fun resistanceProperty() = resistanceProperty

    private val fixedPowerProperty = SimpleBooleanProperty(isFixedPower)
    fun fixedPowerProperty() = fixedPowerProperty
    var isFixedPower by fixedPowerProperty

    private val fixedResistanceProperty = SimpleBooleanProperty(isFixedResistance)
    fun fixedResistanceProperty() = fixedResistanceProperty
    var isFixedResistance by fixedResistanceProperty

    private val verticalAnisotropyCoefficientProperty: DoubleProperty =
        SimpleDoubleProperty(verticalAnisotropyCoefficient)
    var verticalAnisotropyCoefficient by verticalAnisotropyCoefficientProperty
    fun verticalAnisotropyCoefficientProperty() = verticalAnisotropyCoefficientProperty

    private val azimuthProperty: DoubleProperty =
        SimpleDoubleProperty(azimuth)
    var azimuth by azimuthProperty
    fun azimuthProperty() = azimuthProperty

    private val azimuthAnisotropyCoefficientProperty: DoubleProperty =
        SimpleDoubleProperty(azimuthAnisotropyCoefficient)
    var azimuthAnisotropyCoefficient by azimuthAnisotropyCoefficientProperty
    fun azimuthAnisotropyCoefficientProperty() = azimuthAnisotropyCoefficientProperty
}