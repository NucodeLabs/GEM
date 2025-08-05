package ru.nucodelabs.gem.fxmodel.anisotropy

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import tornadofx.getValue
import tornadofx.setValue

class ObservableModelLayer(
    power: ObservableFixableValue<Double>,
    resistance: ObservableFixableValue<Double>,
    verticalAnisotropyCoefficient: ObservableFixableValue<Double>,
    azimuth: ObservableFixableValue<Double>,
    azimuthAnisotropyCoefficient: ObservableFixableValue<Double>,
) {
    private val powerProperty: ObjectProperty<ObservableFixableValue<Double>> = SimpleObjectProperty(power)
    fun powerProperty() = powerProperty
    var power: ObservableFixableValue<Double> by powerProperty

    private val resistanceProperty: ObjectProperty<ObservableFixableValue<Double>> = SimpleObjectProperty(resistance)
    fun resistanceProperty() = resistanceProperty
    var resistance: ObservableFixableValue<Double> by resistanceProperty

    private val verticalAnisotropyCoefficientProperty: ObjectProperty<ObservableFixableValue<Double>> =
        SimpleObjectProperty(verticalAnisotropyCoefficient)

    fun verticalAnisotropyCoefficientProperty() = verticalAnisotropyCoefficientProperty
    var verticalAnisotropyCoefficient: ObservableFixableValue<Double> by verticalAnisotropyCoefficientProperty

    private val azimuthProperty: ObjectProperty<ObservableFixableValue<Double>> =
        SimpleObjectProperty(azimuth)

    fun azimuthProperty() = azimuthProperty
    var azimuth: ObservableFixableValue<Double> by azimuthProperty

    private val azimuthAnisotropyCoefficientProperty: ObjectProperty<ObservableFixableValue<Double>> =
        SimpleObjectProperty(azimuthAnisotropyCoefficient)

    fun azimuthAnisotropyCoefficientProperty() = azimuthAnisotropyCoefficientProperty
    var azimuthAnisotropyCoefficient: ObservableFixableValue<Double> by azimuthAnisotropyCoefficientProperty

}