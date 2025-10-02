package ru.nucodelabs.gem.fxmodel.anisotropy

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import tornadofx.getValue
import tornadofx.setValue

class ObservableModelLayer(
    power: ObservableFixableValue<Double>,
    resistivity: ObservableFixableValue<Double>,
    verticalAnisotropyCoefficient: ObservableFixableValue<Double>,
    azimuth: ObservableFixableValue<Double>,
    azimuthAnisotropyCoefficient: ObservableFixableValue<Double>,
) {
    private val powerProperty: ObjectProperty<ObservableFixableValue<Double>> = SimpleObjectProperty(power)
    fun powerProperty() = powerProperty
    var power: ObservableFixableValue<Double> by powerProperty

    private val resistivityProperty: ObjectProperty<ObservableFixableValue<Double>> = SimpleObjectProperty(resistivity)
    fun resistivityProperty() = resistivityProperty
    var resistivity: ObservableFixableValue<Double> by resistivityProperty

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