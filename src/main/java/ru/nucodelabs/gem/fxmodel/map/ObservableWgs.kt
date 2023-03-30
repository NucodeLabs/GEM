package ru.nucodelabs.gem.fxmodel.map

import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import ru.nucodelabs.kfx.ext.getValue

class ObservableWgs(
    longitudeInDegrees: Double,
    latitudeInDegrees: Double
) {
    private val longitudeInDegreesProperty: ReadOnlyDoubleProperty = SimpleDoubleProperty(longitudeInDegrees)
    val longitudeInDegrees by longitudeInDegreesProperty
    fun longitudeInDegreesProperty() = longitudeInDegreesProperty

    private val latitudeInDegreesProperty: ReadOnlyDoubleProperty = SimpleDoubleProperty(latitudeInDegrees)
    val latitudeInDegrees by latitudeInDegreesProperty
    fun latitudeInDegreesProperty() = latitudeInDegreesProperty
}