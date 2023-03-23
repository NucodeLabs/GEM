package ru.nucodelabs.gem.fxmodel.anisotropy

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import ru.nucodelabs.gem.util.fx.getValue
import ru.nucodelabs.gem.util.fx.setValue

class ObservablePoint(
    azimuthSignals: ObservableList<ObservableAzimuthSignals>,
    model: ObservableList<ObservableModelLayer>
) {
    private val azimuthSignalsProperty: ObjectProperty<ObservableList<ObservableAzimuthSignals>> =
        SimpleObjectProperty(azimuthSignals)
    var azimuthSignals: ObservableList<ObservableAzimuthSignals> by azimuthSignalsProperty
    fun azimuthSignalsProperty() = azimuthSignalsProperty

    private val modelProperty: ObjectProperty<ObservableList<ObservableModelLayer>> = SimpleObjectProperty(model)
    var model: ObservableList<ObservableModelLayer> by modelProperty
    fun modelProperty() = azimuthSignalsProperty
}