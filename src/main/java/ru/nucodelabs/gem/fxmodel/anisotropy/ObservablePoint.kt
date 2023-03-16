package ru.nucodelabs.gem.fxmodel.anisotropy

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import ru.nucodelabs.gem.util.fx.getValue
import ru.nucodelabs.gem.util.fx.setValue

class ObservablePoint(
    azimuthSignals: ObservableList<ObservableAzimuthSignals>
) {
    private val azimuthSignalsProperty: ObjectProperty<ObservableList<ObservableAzimuthSignals>> =
        SimpleObjectProperty(azimuthSignals)
    var azimuthSignals by azimuthSignalsProperty
    fun azimuthSignals() = azimuthSignalsProperty
}