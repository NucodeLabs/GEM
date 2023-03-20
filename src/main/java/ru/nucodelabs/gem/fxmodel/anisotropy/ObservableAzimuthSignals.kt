package ru.nucodelabs.gem.fxmodel.anisotropy

import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import ru.nucodelabs.gem.util.fx.getValue
import ru.nucodelabs.gem.util.fx.setValue

class ObservableAzimuthSignals(
    azimuth: Double,
    signals: ObservableList<ObservableSignal>
) {
    private val azimuthProperty: DoubleProperty = SimpleDoubleProperty(azimuth)
    var azimuth: Double by azimuthProperty
    fun azimuthProperty() = azimuthProperty

    private val signalsProperty: ObjectProperty<ObservableList<ObservableSignal>> = SimpleObjectProperty(signals)
    var signals: ObservableList<ObservableSignal> by signalsProperty
    fun signalsProperty() = signalsProperty
}