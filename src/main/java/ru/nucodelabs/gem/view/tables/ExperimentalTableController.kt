package ru.nucodelabs.gem.view.tables

import jakarta.validation.Validator
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableObjectValue
import javafx.collections.FXCollections.observableList
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.TextFieldTableCell
import javafx.stage.Stage
import javafx.util.Callback
import javafx.util.StringConverter
import ru.nucodelabs.data.ves.ExperimentalData
import ru.nucodelabs.data.ves.Picket
import ru.nucodelabs.data.ves.Section
import ru.nucodelabs.gem.app.model.SectionManager
import ru.nucodelabs.gem.app.snapshot.HistoryManager
import ru.nucodelabs.gem.extensions.fx.emptyBinding
import ru.nucodelabs.gem.utils.FXUtils
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.AlertsFactory
import java.net.URL
import java.text.DecimalFormat
import java.text.ParseException
import java.util.*
import java.util.function.Predicate
import javax.inject.Inject

class ExperimentalTableController @Inject constructor(
    private val picketObservable: ObservableObjectValue<Picket>,
    private val validator: Validator,
    private val sectionManager: SectionManager,
    private val historyManager: HistoryManager<Section>,
    private val alertsFactory: AlertsFactory,
    private val doubleStringConverter: StringConverter<Double>,
    private val decimalFormat: DecimalFormat
) : AbstractController() {

    @FXML
    private lateinit var recalculateBtn: Button

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
    private lateinit var deleteBtn: Button

    @FXML
    private lateinit var table: TableView<ExperimentalData>

    private val picket: Picket
        get() = picketObservable.get()!!

    init {
        picketObservable.addListener { _, oldValue: Picket?, newValue: Picket? ->
            if (newValue != null) {
                if (oldValue != null
                    && oldValue.experimentalData != newValue.experimentalData
                ) {
                    update()
                } else if (oldValue == null) {
                    update()
                }
            }
        }
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        table.selectionModel.selectionMode = SelectionMode.MULTIPLE

        deleteBtn.disableProperty().bind(table.selectionModel.selectedItems.emptyBinding())
        recalculateBtn.disableProperty().bind(table.selectionModel.selectedItems.emptyBinding())

        indexCol.cellFactory = indexCellFactory()
        ab2Col.cellValueFactory = Callback { features -> SimpleObjectProperty(features.value.ab2) }
        mn2Col.cellValueFactory = Callback { features -> SimpleObjectProperty(features.value.mn2) }
        resistanceApparentCol.cellValueFactory =
            Callback { features -> SimpleObjectProperty(features.value.resistanceApparent) }
        errorResistanceCol.cellValueFactory =
            Callback { features -> SimpleObjectProperty(features.value.errorResistanceApparent) }
        amperageCol.cellValueFactory = Callback { features -> SimpleObjectProperty(features.value.amperage) }
        voltageCol.cellValueFactory = Callback { features -> SimpleObjectProperty(features.value.voltage) }

        for (i in 1 until table.columns.size) {
            // safe cast
            table.columns[i].cellFactory = TextFieldTableCell.forTableColumn(doubleStringConverter)
        }

        val validateDataInput = Predicate { s: String -> validateDoubleInput(s, decimalFormat) }
        val validInput = valid(ab2TextField, validateDataInput)
            .and(valid(mn2TextField, validateDataInput))
            .and(valid(resAppTextField, validateDataInput))
            .and(valid(errResAppTextField, validateDataInput))
            .and(valid(voltageTextField, validateDataInput))
            .and(valid(amperageTextField, validateDataInput))
            .and(valid(indexTextField) { s: String -> validateIndexInput(s) })

        val allRequiredNotBlank = FXUtils.isNotBlank(ab2TextField.textProperty())
            .and(FXUtils.isNotBlank(mn2TextField.textProperty()))
            .and(FXUtils.isNotBlank(resAppTextField.textProperty()))
            .and(FXUtils.isNotBlank(errResAppTextField.textProperty()))
            .and(FXUtils.isNotBlank(voltageTextField.textProperty()))
            .and(FXUtils.isNotBlank(amperageTextField.textProperty()))

        addBtn.disableProperty().bind(validInput.not().or(allRequiredNotBlank.not()))

        table.itemsProperty().addListener { _, _, newValue: ObservableList<ExperimentalData> ->
            newValue.addListener(ListChangeListener { table.refresh() })
            table.refresh()
        }
    }

    override val stage: Stage
        get() = table.scene.window as Stage

    private fun update() {
        table.itemsProperty().value = observableList(picket.experimentalData)
        table.refresh()
    }

    @FXML
    private fun deleteSelected() {
        updateIfValidElseAlert(
            picket.experimentalData.toMutableList().apply { removeAllAt(table.selectionModel.selectedIndices) }
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
                indexTextField.text.toInt().coerceAtMost(picket.experimentalData.size)
            } catch (_: NumberFormatException) {
                picket.experimentalData.size
            }

            updateIfValidElseAlert(picket.experimentalData.toMutableList().apply {
                add(
                    index, ExperimentalData.create(
                        newAb2Value,
                        newMn2Value,
                        newResAppValue,
                        newErrResAppValue,
                        newAmperageValue,
                        newVoltageValue
                    )
                )
            })
        }
    }

    private fun updateIfValidElseAlert(newExpData: List<ExperimentalData>) {
        val modified = picket.withExperimentalData(newExpData)
        val violations = validator.validate(modified)
        if (violations.isNotEmpty()) {
            alertsFactory.violationsAlert(violations, stage).show()
            table.refresh()
        } else {
            historyManager.snapshotAfter { sectionManager.update(modified) }
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
            ab2Col -> oldValue.withAb2(newInputValue)
            mn2Col -> oldValue.withMn2(newInputValue)
            resistanceApparentCol -> oldValue.withResistanceApparent(newInputValue)
            errorResistanceCol -> oldValue.withErrorResistanceApparent(newInputValue)
            amperageCol -> oldValue.withAmperage(newInputValue)
            voltageCol -> oldValue.withVoltage(newInputValue)
            else -> throw RuntimeException("Something went wrong!")
        }

        if (!event.newValue.isNaN()) {
            updateIfValidElseAlert(picket.experimentalData.toMutableList().also { it[index] = newValue })
        } else {
            table.refresh()
        }
    }

    @FXML
    private fun recalculateSelected() {
        val experimentalData: MutableList<ExperimentalData> = picket.experimentalData.toMutableList()

        val ind: List<Int> = table.selectionModel.selectedIndices

        for (i in experimentalData.indices) {
            if (i in ind) {
                experimentalData[i] = experimentalData[i].recalculateResistanceApparent()
            }
        }

        historyManager.snapshotAfter {
            sectionManager.update(picket.withExperimentalData(experimentalData))
        }
    }
}