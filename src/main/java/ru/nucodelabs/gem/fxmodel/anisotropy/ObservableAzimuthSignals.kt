package ru.nucodelabs.gem.fxmodel.anisotropy

import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import tornadofx.getValue
import tornadofx.setValue

class ObservableAzimuthSignals(
    azimuth: Double,
    signals: ObservableSignals,
) {
    private val azimuthProperty: DoubleProperty = SimpleDoubleProperty(azimuth)
    var azimuth: Double by azimuthProperty
    fun azimuthProperty() = azimuthProperty

    private val signalsProperty: ObjectProperty<ObservableSignals> = SimpleObjectProperty(signals)
    var signals: ObservableSignals by signalsProperty
    fun signalsProperty() = signalsProperty
}