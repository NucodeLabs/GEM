package ru.nucodelabs.gem.fxmodel.ves

import javafx.beans.property.SimpleBooleanProperty
import ru.nucodelabs.geo.ves.ExperimentalData
import ru.nucodelabs.geo.ves.MutableExperimentalSignal
import ru.nucodelabs.geo.ves.ReadOnlyExperimentalSignal
import ru.nucodelabs.kfx.observable.ConstrainedDoubleProperty
import tornadofx.getValue
import tornadofx.setValue

class ObservableExperimentalData(
    ab2: Double,
    mn2: Double,
    amperage: Double,
    voltage: Double,
    resistivityApparent: Double,
    errorResistivityApparent: Double,
    isHidden: Boolean
) : MutableExperimentalSignal {
    private val ab2Property = ConstrainedDoubleProperty(
        ab2,
        ExperimentalData::isValidDistance
    )

    fun ab2Property() = ab2Property
    override var ab2 by ab2Property

    private val mn2Property = ConstrainedDoubleProperty(
        mn2,
        ExperimentalData::isValidDistance
    )

    fun mn2Property() = mn2Property
    override var mn2 by mn2Property

    private val amperageProperty = ConstrainedDoubleProperty(
        amperage,
        ExperimentalData::isValidAmperage
    )

    fun amperageProperty() = amperageProperty
    override var amperage by amperageProperty

    private val voltageProperty = ConstrainedDoubleProperty(
        voltage,
        ExperimentalData::isValidVoltage
    )

    fun voltageProperty() = voltageProperty
    override var voltage by voltageProperty

    private val resistivityApparentProperty = ConstrainedDoubleProperty(
        resistivityApparent,
        ExperimentalData::isValidResistApparent
    )

    fun resistivityApparentProperty() = resistivityApparentProperty
    override var resistivityApparent by resistivityApparentProperty

    private val errorResistivityApparentProperty = ConstrainedDoubleProperty(
        errorResistivityApparent,
        ExperimentalData::isValidErrResistApparent
    )

    fun errorResistivityApparentProperty() = errorResistivityApparentProperty
    override var errorResistivityApparent by errorResistivityApparentProperty

    private val hiddenProperty = SimpleBooleanProperty(isHidden)
    fun hiddenProperty() = hiddenProperty
    override var isHidden by hiddenProperty
}

fun ReadOnlyExperimentalSignal.toObservable() =
    ObservableExperimentalData(ab2, mn2, amperage, voltage, resistivityApparent, errorResistivityApparent, isHidden)