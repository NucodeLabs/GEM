package ru.nucodelabs.gem.fxmodel.anisotropy

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import tornadofx.getValue
import tornadofx.setValue

class ObservableSignals(
    sortedSignals: ObservableList<ObservableSignal>,
    effectiveSignals: ObservableList<ObservableSignal>
) {
    private val effectiveSignalsProperty: ObjectProperty<ObservableList<ObservableSignal>> =
        SimpleObjectProperty(effectiveSignals)
    var effectiveSignals: ObservableList<ObservableSignal> by effectiveSignalsProperty
    fun effectiveSignalsProperty() = effectiveSignalsProperty

    private val sortedSignalsProperty: ObjectProperty<ObservableList<ObservableSignal>> =
        SimpleObjectProperty(sortedSignals)
    var sortedSignals: ObservableList<ObservableSignal> by sortedSignalsProperty
    fun sortedSignalsProperty() = sortedSignalsProperty
}