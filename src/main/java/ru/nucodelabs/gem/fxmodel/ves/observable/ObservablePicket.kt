package ru.nucodelabs.gem.fxmodel.ves.observable

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import ru.nucodelabs.gem.fxmodel.ves.ObservableExperimentalData
import ru.nucodelabs.gem.fxmodel.ves.ObservableModelLayer
import ru.nucodelabs.geo.ves.*
import ru.nucodelabs.geo.ves.calc.orderByDistances
import ru.nucodelabs.kfx.observable.ConstrainedDoubleProperty
import tornadofx.getValue
import tornadofx.setValue

class ObservablePicket : MutableExperimentalDataSet<ObservableExperimentalData>, ModelDataSet {
    val nameProperty: StringProperty = SimpleStringProperty(Picket.DEFAULT_NAME)
    var name by nameProperty
    fun nameProperty() = nameProperty

    val rawExperimentalData: ObservableList<ObservableExperimentalData> =
        FXCollections.observableArrayList { experimentalData ->
            arrayOf(
                experimentalData.ab2Property(),
                experimentalData.mn2Property(),
                experimentalData.amperageProperty(),
                experimentalData.voltageProperty(),
                experimentalData.resistivityApparentProperty(),
                experimentalData.errorResistivityApparentProperty()
            )
        }

    override val modelData: ObservableList<ObservableModelLayer> =
        FXCollections.observableArrayList<ObservableModelLayer>().apply {
            addListener(ListChangeListener { change ->
                while (change.next()) {
                    if (change.wasAdded() && !Picket.isValidModelDataSize(change.list.size)) {
                        throw InvalidPropertiesException(
                            listOf(
                                InvalidPropertyValue("modelData.size", "", change.list.size)
                            )
                        )
                    }
                }
            })
        }

    val offsetXProperty: DoubleProperty = ConstrainedDoubleProperty(Picket.DEFAULT_X_OFFSET, Picket::isValidOffsetX)
    override var offsetX by offsetXProperty
    fun offsetXProperty() = offsetXProperty

    val modelZProperty: DoubleProperty = SimpleDoubleProperty(Picket.DEFAULT_Z)
    override var modelZ by modelZProperty
    fun modelZProperty() = modelZProperty

    val commentProperty: StringProperty = SimpleStringProperty(Picket.DEFAULT_COMMENT)
    var comment by commentProperty
    fun commentProperty() = commentProperty

    override val sortedExperimentalData: List<ReadOnlyExperimentalSignal> =
        rawExperimentalData.sorted(orderByDistances()).apply {
            addListener(ListChangeListener { change ->
                while (change.next()) {
                    when {
                        change.wasUpdated() -> handleExperimentalDataUpdate(change.list)
                        change.wasAdded() -> handleExperimentalDataUpdate(change.list)
                        change.wasReplaced() -> handleExperimentalDataUpdate(change.list)
                    }
                }
            })
        }

    private fun handleExperimentalDataUpdate(list: List<MutableExperimentalSignal>) {
        hideExtraSignalsInPlace(list)
    }

    override val effectiveExperimentalData: List<ReadOnlyExperimentalSignal>
        get() = sortedExperimentalData.filter { !it.isHidden }

    override fun addSignal(signal: ObservableExperimentalData) {
        rawExperimentalData.add(signal)
    }

    override fun addSignals(signals: Iterable<ObservableExperimentalData>) {
        rawExperimentalData.addAll(signals)
    }

    override fun removeSignal(signal: ObservableExperimentalData) {
        rawExperimentalData.remove(signal)
    }

    override fun removeSignals(signals: Iterable<ObservableExperimentalData>) {
        rawExperimentalData.removeAll(signals)
    }

    override fun edit(index: Int, mutate: MutableExperimentalSignal.() -> Unit) {
        rawExperimentalData[index].mutate()
    }
}