package ru.nucodelabs.gem.view.tables

import jakarta.validation.Validator
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableObjectValue
import javafx.collections.FXCollections.observableList
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.TextFieldTableCell
import javafx.stage.Stage
import javafx.util.Callback
import javafx.util.StringConverter
import ru.nucodelabs.data.fx.ObservableSection
import ru.nucodelabs.data.ves.ExperimentalData
import ru.nucodelabs.data.ves.Picket
import ru.nucodelabs.data.ves.Section
import ru.nucodelabs.data.ves.withCalculatedResistanceApparent
import ru.nucodelabs.gem.app.snapshot.HistoryManager
import ru.nucodelabs.gem.extensions.fx.isNotBlank
import ru.nucodelabs.gem.extensions.fx.isValidBy
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
    private lateinit var indexCol: TableColumn<Any, Int>

    @FXML
    private lateinit var ab2Col: TableColumn<ExperimentalData, Double>

    @FXML
    private lateinit var mn2Col: TableColumn<ExperimentalData, Double>

    @FXML
    private lateinit var resistanceApparentCol: TableColumn<ExperimentalData, Double>

    @FXML
    private lateinit var errorResistanceCol: TableColumn<ExperimentalData, Double>

    @FXML
    private lateinit var amperageCol: TableColumn<ExperimentalData, Double>

    @FXML
    private lateinit var voltageCol: TableColumn<ExperimentalData, Double>

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
    private lateinit var table: TableView<ExperimentalData>

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

        table.itemsProperty().addListener { _, _, newValue: ObservableList<ExperimentalData> ->
            newValue.addListener(ListChangeListener { table.refresh() })
            table.refresh()
        }
    }

    private fun setupCellFactories() {
        indexCol.cellFactory = indexCellFactory()
        ab2Col.cellValueFactory = Callback { features -> SimpleObjectProperty(features.value.ab2) }
        mn2Col.cellValueFactory = Callback { features -> SimpleObjectProperty(features.value.mn2) }
        resistanceApparentCol.cellValueFactory =
            Callback { features -> SimpleObjectProperty(features.value.resistanceApparent) }
        errorResistanceCol.cellValueFactory =
            Callback { features -> SimpleObjectProperty(features.value.errorResistanceApparent) }
        amperageCol.cellValueFactory = Callback { features -> SimpleObjectProperty(features.value.amperage) }
        voltageCol.cellValueFactory = Callback { features -> SimpleObjectProperty(features.value.voltage) }

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
            TableRow<ExperimentalData>().apply {
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
        table.itemsProperty().value = observableList(picket.sortedExperimentalData)
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
    private fun onEditCommit(event: TableColumn.CellEditEvent<ExperimentalData, Double>) {
        val index = event.tablePosition.row
        val newInputValue = event.newValue
        val oldValue = event.rowValue

        val newValue: ExperimentalData = when (event.tableColumn) {
            ab2Col -> oldValue.copy(ab2 = newInputValue)
            mn2Col -> oldValue.copy(ab2 = newInputValue)
            resistanceApparentCol -> oldValue.copy(resistanceApparent = newInputValue)
            errorResistanceCol -> oldValue.copy(errorResistanceApparent = newInputValue)
            amperageCol -> oldValue.copy(amperage = newInputValue)
            voltageCol -> oldValue.copy(voltage = newInputValue)
            else -> throw RuntimeException("Something went wrong!")
        }

        if (!event.newValue.isNaN()) {
            updateIfValidElseAlert(picket.sortedExperimentalData.toMutableList().also { it[index] = newValue })
        } else {
            table.refresh()
        }
    }

    @FXML
    private fun recalculateSelected() {
        val experimentalData: MutableList<ExperimentalData> = picket.sortedExperimentalData.toMutableList()

        val ind: List<Int> = table.selectionModel.selectedIndices

        for (i in experimentalData.indices) {
            if (i in ind) {
                experimentalData[i] = experimentalData[i].withCalculatedResistanceApparent()
            }
        }

        historyManager.snapshotAfter {
            observableSection.pickets[picketIndex] = picket.copy(experimentalData = experimentalData)
        }
    }
}