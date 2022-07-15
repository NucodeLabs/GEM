package ru.nucodelabs.gem.view.tables

import jakarta.validation.Validator
import javafx.beans.property.IntegerProperty
import javafx.beans.value.ObservableObjectValue
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.TextFieldTableCell
import javafx.stage.Stage
import javafx.util.Callback
import javafx.util.StringConverter
import ru.nucodelabs.data.fx.ObservableExperimentalData
import ru.nucodelabs.data.fx.ObservableSection
import ru.nucodelabs.data.fx.toObservable
import ru.nucodelabs.data.ves.ExperimentalData
import ru.nucodelabs.data.ves.Picket
import ru.nucodelabs.data.ves.Section
import ru.nucodelabs.data.ves.withCalculatedResistanceApparent
import ru.nucodelabs.gem.app.snapshot.HistoryManager
import ru.nucodelabs.gem.extensions.fx.isNotBlank
import ru.nucodelabs.gem.extensions.fx.isValidBy
import ru.nucodelabs.gem.extensions.fx.toObservableList
import ru.nucodelabs.gem.extensions.std.removeAllAt
import ru.nucodelabs.gem.util.FXUtils
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.AlertsFactory
import java.net.URL
import java.text.DecimalFormat
import java.text.ParseException
import java.util.*
import javax.inject.Inject

class ExperimentalTableController @Inject constructor(
    private val picketObservable: ObservableObjectValue<Picket>,
    private val validator: Validator,
    private val observableSection: ObservableSection,
    private val _picketIndex: IntegerProperty,
    private val historyManager: HistoryManager<Section>,
    private val alertsFactory: AlertsFactory,
    private val doubleStringConverter: StringConverter<Double>,
    private val decimalFormat: DecimalFormat
) : AbstractController() {

    @FXML
    lateinit var isHiddenCol: TableColumn<ObservableExperimentalData, Boolean>

    @FXML
    private lateinit var indexCol: TableColumn<Any, Int>

    @FXML
    private lateinit var ab2Col: TableColumn<ObservableExperimentalData, Double>

    @FXML
    private lateinit var mn2Col: TableColumn<ObservableExperimentalData, Double>

    @FXML
    private lateinit var resistanceApparentCol: TableColumn<ObservableExperimentalData, Double>

    @FXML
    private lateinit var errorResistanceCol: TableColumn<ObservableExperimentalData, Double>

    @FXML
    private lateinit var amperageCol: TableColumn<ObservableExperimentalData, Double>

    @FXML
    private lateinit var voltageCol: TableColumn<ObservableExperimentalData, Double>

    @FXML
    private lateinit var indexTextField: TextField

    @FXML
    private lateinit var ab2TextField: TextField

    @FXML
    private lateinit var mn2TextField: TextField

    @FXML
    private lateinit var resAppTextField: TextField

    @FXML
    private lateinit var errResAppTextField: TextField

    @FXML
    private lateinit var amperageTextField: TextField

    @FXML
    private lateinit var voltageTextField: TextField

    @FXML
    private lateinit var addBtn: Button

    @FXML
    private lateinit var table: TableView<ObservableExperimentalData>

    override val stage: Stage
        get() = table.scene.window as Stage

    private val picket: Picket
        get() = picketObservable.get()!!

    private val picketIndex
        get() = _picketIndex.get()


    override fun initialize(location: URL, resources: ResourceBundle) {
        picketObservable.addListener { _, oldValue: Picket?, newValue: Picket? ->
            if (newValue != null) {
                if (oldValue != null
                    && oldValue.sortedExperimentalData != newValue.sortedExperimentalData
                    && newValue.sortedExperimentalData != table.items.map { it.asExperimentalData() }
                ) {
                    update()
                } else if (oldValue == null) {
                    update()
                }
            }
        }

        table.selectionModel.selectionMode = SelectionMode.MULTIPLE

        setupCellFactories()
        setupRowFactory()
        setupValidation()

        table.itemsProperty().addListener { _, _, newValue: ObservableList<ObservableExperimentalData> ->
            newValue.addListener(ListChangeListener { table.refresh() })
            table.refresh()
        }
    }

    private fun setupCellFactories() {
        indexCol.cellFactory = indexCellFactory()

        isHiddenCol.cellValueFactory = Callback { features -> features.value.hiddenProperty() }
        isHiddenCol.cellFactory = CheckBoxTableCell.forTableColumn(isHiddenCol)

        ab2Col.cellValueFactory = Callback { features -> features.value.ab2Property().asObject() }
        mn2Col.cellValueFactory = Callback { features -> features.value.mn2Property().asObject() }
        resistanceApparentCol.cellValueFactory =
            Callback { features -> features.value.resistanceApparentProperty().asObject() }
        errorResistanceCol.cellValueFactory =
            Callback { features -> features.value.errorResistanceApparentProperty().asObject() }
        amperageCol.cellValueFactory = Callback { features -> features.value.amperageProperty().asObject() }
        voltageCol.cellValueFactory = Callback { features -> features.value.voltageProperty().asObject() }

        val editableColumns = listOf(
            ab2Col,
            mn2Col,
            resistanceApparentCol,
            errorResistanceCol,
            amperageCol,
            voltageCol
        )
        editableColumns.forEach { it.cellFactory = TextFieldTableCell.forTableColumn(doubleStringConverter) }
    }

    private fun setupValidation() {
        val validateDataInput = { s: String -> validateDoubleInput(s, decimalFormat) }
        val validInput = ab2TextField.isValidBy { validateDataInput(it) }
            .and(mn2TextField.isValidBy { validateDataInput(it) })
            .and(resAppTextField.isValidBy { validateDataInput(it) })
            .and(errResAppTextField.isValidBy { validateDataInput(it) })
            .and(voltageTextField.isValidBy { validateDataInput(it) })
            .and(amperageTextField.isValidBy { validateDataInput(it) })
            .and(indexTextField.isValidBy { validateIndexInput(it) })

        val allRequiredNotBlank = ab2TextField.textProperty().isNotBlank()
            .and(mn2TextField.textProperty().isNotBlank())
            .and(resAppTextField.textProperty().isNotBlank())
            .and(errResAppTextField.textProperty().isNotBlank())
            .and(voltageTextField.textProperty().isNotBlank())
            .and(amperageTextField.textProperty().isNotBlank())

        addBtn.disableProperty().bind(validInput.not().or(allRequiredNotBlank.not()))
    }

    private fun setupRowFactory() {
        table.rowFactory = Callback { _ ->
            TableRow<ObservableExperimentalData>().apply {
                val contextMenu = ContextMenu(
                    MenuItem("Удалить").apply {
                        onAction = EventHandler { deleteSelected() }
                    },
                    MenuItem("Рассчитать ρₐ").apply {
                        onAction = EventHandler { recalculateSelected() }
                    }
                ).apply {
                    style = "-fx-font-size: $DEFAULT_FONT_SIZE;"
                }

                onContextMenuRequested = EventHandler { contextMenu.show(this, it.screenX, it.screenY) }
            }
        }
    }

    private fun update() {
        table.itemsProperty().value =
            picket.sortedExperimentalData.map { it.toObservable() }.toObservableList()
        table.items.forEachIndexed { index, expData ->
            expData.ab2Property().addListener { _, oldAb2, newAb2 ->
                if (validator.validateValue(ExperimentalData::class.java, "ab2", newAb2).isEmpty()) {
                    historyManager.snapshotAfter {
                        observableSection.pickets[picketIndex] =
                            picket.copy(experimentalData = picket.sortedExperimentalData.toMutableList().apply {
                                set(index, expData.asExperimentalData().copy(ab2 = newAb2.toDouble()))
                            })
                    }
                } else {
                    expData.ab2 = oldAb2.toDouble()
                }
            }
            expData.mn2Property().addListener { _, oldMn2, newMn2 ->
                if (validator.validateValue(ExperimentalData::class.java, "mn2", newMn2).isEmpty()) {
                    historyManager.snapshotAfter {
                        observableSection.pickets[picketIndex] =
                            picket.copy(experimentalData = picket.sortedExperimentalData.toMutableList().apply {
                                set(index, expData.asExperimentalData().copy(mn2 = newMn2.toDouble()))
                            })
                    }
                } else {
                    expData.mn2 = oldMn2.toDouble()
                }
            }
            expData.errorResistanceApparentProperty().addListener { _, oldErr, newErr ->
                if (validator.validateValue(ExperimentalData::class.java, "errorResistanceApparent", newErr)
                        .isEmpty()
                ) {
                    historyManager.snapshotAfter {
                        observableSection.pickets[picketIndex] =
                            picket.copy(experimentalData = picket.sortedExperimentalData.toMutableList().apply {
                                set(
                                    index,
                                    expData.asExperimentalData().copy(errorResistanceApparent = newErr.toDouble())
                                )
                            })
                    }
                } else {
                    expData.errorResistanceApparent = oldErr.toDouble()
                }
            }
            expData.amperageProperty().addListener { _, oldAmp, newAmp ->
                if (validator.validateValue(ExperimentalData::class.java, "amperage", newAmp).isEmpty()) {
                    historyManager.snapshotAfter {
                        observableSection.pickets[picketIndex] =
                            picket.copy(experimentalData = picket.sortedExperimentalData.toMutableList().apply {
                                set(index, expData.asExperimentalData().copy(amperage = newAmp.toDouble()))
                            })
                    }
                } else {
                    expData.amperage = oldAmp.toDouble()
                }
            }
            expData.voltageProperty().addListener { _, oldVolt, newVolt ->
                if (validator.validateValue(ExperimentalData::class.java, "voltage", newVolt).isEmpty()) {
                    historyManager.snapshotAfter {
                        observableSection.pickets[picketIndex] =
                            picket.copy(experimentalData = picket.sortedExperimentalData.toMutableList().apply {
                                set(index, expData.asExperimentalData().copy(voltage = newVolt.toDouble()))
                            })
                    }
                } else {
                    expData.voltage = oldVolt.toDouble()
                }
            }
            expData.resistanceApparentProperty().addListener { _, oldRes, newRes ->
                if (validator.validateValue(ExperimentalData::class.java, "resistanceApparent", newRes).isEmpty()) {
                    historyManager.snapshotAfter {
                        observableSection.pickets[picketIndex] =
                            picket.copy(experimentalData = picket.sortedExperimentalData.toMutableList().apply {
                                set(index, expData.asExperimentalData().copy(resistanceApparent = newRes.toDouble()))
                            })
                    }
                } else {
                    expData.resistanceApparent = oldRes.toDouble()
                }
            }
            expData.hiddenProperty().addListener { _, _, newHidden ->
                historyManager.snapshotAfter {
                    observableSection.pickets[picketIndex] =
                        picket.copy(experimentalData = picket.sortedExperimentalData.toMutableList().apply {
                            set(index, expData.asExperimentalData().copy(isHidden = newHidden))
                        })
                }
            }
        }
        table.refresh()
    }

    @FXML
    private fun deleteSelected() {
        updateIfValidElseAlert(
            picket.sortedExperimentalData.toMutableList().apply { removeAllAt(table.selectionModel.selectedIndices) }
        )
    }

    @FXML
    private fun add() {
        if (!addBtn.isDisable) {
            val newAb2Value: Double
            val newMn2Value: Double
            val newResAppValue: Double
            val newErrResAppValue: Double
            val newAmperageValue: Double
            val newVoltageValue: Double
            try {
                newAb2Value = decimalFormat.parse(ab2TextField.text).toDouble()
                newMn2Value = decimalFormat.parse(mn2TextField.text).toDouble()
                newResAppValue = decimalFormat.parse(resAppTextField.text).toDouble()
                newErrResAppValue = decimalFormat.parse(errResAppTextField.text).toDouble()
                newAmperageValue = decimalFormat.parse(amperageTextField.text).toDouble()
                newVoltageValue = decimalFormat.parse(voltageTextField.text).toDouble()
            } catch (e: ParseException) {
                return
            }

            val index = try {
                indexTextField.text.toInt().coerceAtMost(picket.sortedExperimentalData.size)
            } catch (_: NumberFormatException) {
                picket.sortedExperimentalData.size
            }

            updateIfValidElseAlert(picket.sortedExperimentalData.toMutableList().apply {
                add(
                    index,
                    ExperimentalData(
                        ab2 = newAb2Value,
                        mn2 = newMn2Value,
                        resistanceApparent = newResAppValue,
                        errorResistanceApparent = newErrResAppValue,
                        amperage = newAmperageValue,
                        voltage = newVoltageValue
                    )
                )
            })
        }
    }

    private fun updateIfValidElseAlert(newExpData: List<ExperimentalData>) {
        val modified = picket.copy(experimentalData = newExpData)
        val violations = validator.validate(modified)
        if (violations.isNotEmpty()) {
            alertsFactory.violationsAlert(violations, stage).show()
            table.refresh()
        } else {
            historyManager.snapshotAfter { observableSection.pickets[picketIndex] = modified }
            FXUtils.unfocus(
                indexTextField,
                ab2TextField,
                mn2TextField,
                resAppTextField,
                errResAppTextField,
                amperageTextField,
                voltageTextField
            )
        }
    }

    @FXML
    private fun recalculateSelected() {
        val experimentalData = picket.sortedExperimentalData.toMutableList()

        for (i in table.selectionModel.selectedIndices) {
            experimentalData[i] = table.items[i].asExperimentalData().withCalculatedResistanceApparent()
        }

        historyManager.snapshotAfter {
            observableSection.pickets[picketIndex] = picket.copy(experimentalData = experimentalData)
        }
    }
}