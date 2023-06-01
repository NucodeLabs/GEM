package ru.nucodelabs.gem.fxmodel.anisotropy

import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import ru.nucodelabs.gem.util.fx.getValue
import ru.nucodelabs.gem.util.fx.setValue

class ObservableModelLayer(
    power: ObservableFixableValue<Double>,
    resistance: ObservableFixableValue<Double>,
    verticalAnisotropyCoefficient: ObservableFixableValue<Double>,
    azimuth: Double,
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

    private val azimuthProperty: DoubleProperty =
        SimpleDoubleProperty(azimuth)
    var azimuth by azimuthProperty
    fun azimuthProperty() = azimuthProperty

    private val azimuthAnisotropyCoefficientProperty: ObjectProperty<ObservableFixableValue<Double>> =
        SimpleObjectProperty(azimuthAnisotropyCoefficient)

    fun azimuthAnisotropyCoefficientProperty() = azimuthAnisotropyCoefficientProperty
    var azimuthAnisotropyCoefficient: ObservableFixableValue<Double> by azimuthAnisotropyCoefficientProperty

}