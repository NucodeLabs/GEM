package ru.nucodelabs.gem.view.controller.main

import jakarta.inject.Inject
import javafx.beans.binding.Bindings
import javafx.beans.property.IntegerProperty
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.layout.VBox
import javafx.util.StringConverter
import ru.nucodelabs.gem.fxmodel.ves.ObservableSection
import ru.nucodelabs.gem.view.AlertsFactory
import ru.nucodelabs.geo.ves.ExperimentalData
import ru.nucodelabs.geo.ves.InvalidPropertiesException
import ru.nucodelabs.geo.ves.InvalidPropertyValue
import ru.nucodelabs.geo.ves.Section
import ru.nucodelabs.geo.ves.calc.rhoA
import ru.nucodelabs.kfx.core.AbstractViewController
import ru.nucodelabs.kfx.snapshot.HistoryManager
import tornadofx.get
import java.net.URL
import java.text.DecimalFormat
import java.text.ParseException
import java.util.*

class AddExperimentalDataController @Inject constructor(
    private val alertsFactory: AlertsFactory,
    private val picketIndex: IntegerProperty,
    private val observableSection: ObservableSection,
    private val historyManager: HistoryManager<Section>,
    private val decimalFormat: DecimalFormat,
    private val uiProps: ResourceBundle
) : AbstractViewController<VBox>() {

    @FXML
    private lateinit var invalidMessageLabel: Label

    @FXML
    private lateinit var amperageTextField: TextField

    @FXML
    private lateinit var errResAppTextField: TextField

    @FXML
    private lateinit var voltageTextField: TextField

    @FXML
    private lateinit var resAppTextField: TextField

    @FXML
    private lateinit var mn2TextField: TextField

    @FXML
    private lateinit var ab2TextField: TextField

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        val fields = listOf(
            ab2TextField to ExperimentalData::validateAb2,
            mn2TextField to ExperimentalData::validateMn2,
            resAppTextField to ExperimentalData::validateResistApparent,
            errResAppTextField to ExperimentalData::validateErrResistApparent,
            voltageTextField to ExperimentalData::validateVoltage,
            amperageTextField to ExperimentalData::validateAmperage
        )
        val converter = object : StringConverter<Double>() {
            override fun toString(value: Double?): String? = value?.let {
                decimalFormat.format(it)
            }

            override fun fromString(value: String?): Double? {
                if (value.isNullOrBlank()) return null
                return try {
                    decimalFormat.parse(value).toDouble()
                } catch (_: ParseException) {
                    null
                }
            }
        }
        fields.forEach { (field, _) -> field.textFormatter = TextFormatter(converter) }
        invalidMessageLabel.textProperty().bind(
            Bindings.createStringBinding(
                { invalidInputMessage(fields) },
                *fields.map { (field, _) -> field.textProperty() }.toTypedArray()
            )
        )
        invalidMessageLabel.visibleProperty().bind(invalidMessageLabel.textProperty().map { it.isNotBlank() })
    }

    private fun invalidInputMessage(fields: List<Pair<TextField, (Double) -> InvalidPropertyValue?>>): String =
        fields.map { (field, validate) ->
            val value = field.textFormatter.value as Double?
            return@map if (value == null) {
                // Fill errorResistivityApparent default value if absent
                if (field === errResAppTextField) {
                    @Suppress("unchecked_cast")
                    (field.textFormatter as TextFormatter<Double>).value = ExperimentalData.DEFAULT_ERROR
                    return@map ""
                }
                // Fill resistivityApparent formula calculated default value if absent
                if (field === resAppTextField) {
                    val ab2 = ab2TextField.textFormatter.value as Double?
                    val mn2 = mn2TextField.textFormatter.value as Double?
                    val amperage = amperageTextField.textFormatter.value as Double?
                    val voltage = voltageTextField.textFormatter.value as Double?
                    if (ab2 != null && mn2 != null && amperage != null && voltage != null) {
                        @Suppress("unchecked_cast")
                        (field.textFormatter as TextFormatter<Double>)
                            .value = rhoA(ab2, mn2, amperage, voltage)
                        return@map ""
                    }
                }
                "${field.promptText}: ${uiProps["invalidInput"]}"
            } else {
                validate(value)?.let { (prop, _, _) -> uiProps["invalid.exp.$prop"] } ?: ""
            }
        }.filter { it.isNotBlank() }.joinToString(separator = "\n")

    @FXML
    private fun add() {
        val newExpDataItem = try {
            ExperimentalData(
                ab2 = ab2TextField.textFormatter.value as Double,
                mn2 = mn2TextField.textFormatter.value as Double,
                amperage = amperageTextField.textFormatter.value as Double,
                resistanceApparent = resAppTextField.textFormatter.value as Double,
                voltage = voltageTextField.textFormatter.value as Double,
                errorResistanceApparent = errResAppTextField.textFormatter.value as Double
            )
        } catch (e: InvalidPropertiesException) {
            alertsFactory.simpleExceptionAlert(e, stage).show()
            return
        }

        historyManager.snapshotAfter {
            val picket = observableSection.pickets[picketIndex.value]
            val expData = picket.sortedExperimentalData
            observableSection.pickets[picketIndex.value] =
                picket.copy(experimentalData = expData.toMutableList().apply {
                    add(newExpDataItem)
                })
        }
    }
}