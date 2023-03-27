package ru.nucodelabs.gem.fxmodel.anisotropy

import javafx.beans.property.*
import javafx.collections.ObservableList
import ru.nucodelabs.gem.util.fx.getValue
import ru.nucodelabs.gem.util.fx.setValue

class ObservablePoint(
    azimuthSignals: ObservableList<ObservableAzimuthSignals>,
    model: ObservableList<ObservableModelLayer>,
    z: Double,
    comment: String
) {
    private val azimuthSignalsProperty: ObjectProperty<ObservableList<ObservableAzimuthSignals>> =
        SimpleObjectProperty(azimuthSignals)
    var azimuthSignals: ObservableList<ObservableAzimuthSignals> by azimuthSignalsProperty
    fun azimuthSignalsProperty() = azimuthSignalsProperty

    private val modelProperty: ObjectProperty<ObservableList<ObservableModelLayer>> = SimpleObjectProperty(model)
    var model: ObservableList<ObservableModelLayer> by modelProperty
    fun modelProperty() = azimuthSignalsProperty

    private val zProperty: DoubleProperty = SimpleDoubleProperty(z)
    var z: Double by zProperty
    fun zProperty() = zProperty

    private val commentProperty: StringProperty = SimpleStringProperty(comment)
    var comment: String by commentProperty
    fun commentProperty() = commentProperty
}