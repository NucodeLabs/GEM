package ru.nucodelabs.data.fx

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import ru.nucodelabs.data.ves.ExperimentalData
import ru.nucodelabs.gem.extensions.fx.getValue
import ru.nucodelabs.gem.extensions.fx.setValue

class ObservableExperimentalData(
    ab2: Double,
    mn2: Double,
    amperage: Double,
    voltage: Double,
    resistanceApparent: Double,
    errorResistanceApparent: Double,
    isHidden: Boolean = false
) {
    constructor(experimentalData: ExperimentalData) : this(
        experimentalData.ab2,
        experimentalData.mn2,
        experimentalData.amperage,
        experimentalData.voltage,
        experimentalData.resistanceApparent,
        experimentalData.errorResistanceApparent,
        experimentalData.isHidden
    )

    fun toExperimentalData() =
        ExperimentalData(ab2, mn2, amperage, voltage, resistanceApparent, errorResistanceApparent, isHidden)

    private val ab2Property = SimpleDoubleProperty(ab2)
    fun ab2Property() = ab2Property
    var ab2 by ab2Property

    private val mn2Property = SimpleDoubleProperty(mn2)
    fun mn2Property() = mn2Property
    var mn2 by mn2Property

    private val amperageProperty = SimpleDoubleProperty(amperage)
    fun amperageProperty() = amperageProperty
    var amperage by amperageProperty

    private val voltageProperty = SimpleDoubleProperty(voltage)
    fun voltageProperty() = voltageProperty
    var voltage by voltageProperty

    private val resistanceApparentProperty = SimpleDoubleProperty(resistanceApparent)
    fun resistanceApparentProperty() = resistanceApparentProperty
    var resistanceApparent by resistanceApparentProperty

    private val errorResistanceApparentProperty = SimpleDoubleProperty(errorResistanceApparent)
    fun errorResistanceApparentProperty() = errorResistanceApparentProperty
    var errorResistanceApparent by errorResistanceApparentProperty

    private val hiddenProperty = SimpleBooleanProperty(isHidden)
    fun hiddenProperty() = hiddenProperty
    var isHidden by hiddenProperty
}

fun ExperimentalData.toObservable() = ObservableExperimentalData(this)