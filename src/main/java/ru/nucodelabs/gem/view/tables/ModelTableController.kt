package ru.nucodelabs.gem.view.tables

import jakarta.validation.Validator
import javafx.beans.binding.Bindings.createStringBinding
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableObjectValue
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import javafx.stage.Stage
import javafx.util.Callback
import javafx.util.StringConverter
import ru.nucodelabs.data.ves.ModelLayer
import ru.nucodelabs.data.ves.Picket
import ru.nucodelabs.data.ves.Section
import ru.nucodelabs.gem.app.model.SectionManager
import ru.nucodelabs.gem.app.snapshot.HistoryManager
import ru.nucodelabs.gem.utils.FXUtils
import ru.nucodelabs.gem.utils.FXUtils.isNotBlank
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.AlertsFactory
import ru.nucodelabs.gem.view.main.MainViewController
import ru.nucodelabs.gem.view.tables.Tables.*
import java.net.URL
import java.text.DecimalFormat
import java.text.ParseException
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class ModelTableController @Inject constructor(
    private val picketObservable: ObservableObjectValue<Picket?>,
    private val mainViewControllerProvider: Provider<MainViewController>,
    private val alertsFactory: AlertsFactory,
    private val validator: Validator,
    private val sectionManager: SectionManager,
    private val historyManager: HistoryManager<Section>,
    private val doubleStringConverter: StringConverter<Double>,
    private val decimalFormat: DecimalFormat
) : AbstractController() {

    @FXML
    private lateinit var zCol: TableColumn<ModelLayer, Double>

    @FXML
    private lateinit var indexCol: TableColumn<Any, Int>

    @FXML
    private lateinit var powerCol: TableColumn<ModelLayer, Double>

    @FXML
    private lateinit var resistanceCol: TableColumn<ModelLayer, Double>

    @FXML
    private lateinit var powerTextField: TextField

    @FXML
    private lateinit var resistanceTextField: TextField

    @FXML
    private lateinit var indexTextField: TextField

    @FXML
    private lateinit var deleteBtn: Button

    @FXML
    private lateinit var addBtn: Button

    @FXML
    private lateinit var table: TableView<ModelLayer>

    private val picket: Picket
        get() = picketObservable.get()!!

    override fun initialize(location: URL, resources: ResourceBundle) {
        picketObservable.addListener { _, oldValue, newValue ->
            newValue?.let {
                if (oldValue != null
                    && oldValue.modelData != it.modelData
                ) {
                    update()
                } else if (oldValue == null) {
                    update()
                }
            }
        }

        table.selectionModel.selectionMode = SelectionMode.MULTIPLE
        table.selectionModel.selectedItems.addListener(ListChangeListener {
            if (it.next()) {
                deleteBtn.isDisable = it.list.isEmpty()
            }
        })
        indexCol.cellFactory = indexCellFactory()
        powerCol.cellValueFactory = Callback { features -> SimpleObjectProperty(features.value.power) }
        resistanceCol.cellValueFactory = Callback { features -> SimpleObjectProperty(features.value.resistance) }
        zCol.cellFactory = Callback {
            TableCell<ModelLayer, Double>().apply {
                textProperty().bind(
                    createStringBinding(
                        {
                            if (!isEmpty && index >= 0
                                && picketObservable.get() != null
                                && index < picket.modelData.size
                            ) {
                                decimalFormat.format(picket.zOfModelLayers()[index])
                            } else {
                                ""
                            }
                        },
                        emptyProperty(), indexProperty(), picketObservable
                    )
                )
            }
        }

        for (i in 1 until table.columns.size - 1) {
            table.columns[i].cellFactory = TextFieldTableCell.forTableColumn(doubleStringConverter)
        }

        val validInput = valid(indexTextField) { validateIndexInput(it) }
            .and(valid(resistanceTextField) { validateDoubleInput(it, decimalFormat) })
            .and(valid(powerTextField) { validateDoubleInput(it, decimalFormat) })

        val allRequiredNotBlank = isNotBlank(powerTextField.textProperty())
            .and(isNotBlank(resistanceTextField.textProperty()))

        addBtn.disableProperty().bind(validInput.not().or(allRequiredNotBlank.not()))

        table.itemsProperty().addListener { _, _, newValue: ObservableList<ModelLayer> ->
            newValue.addListener(ListChangeListener { table.refresh() })
            table.refresh()
        }
    }

    override val stage: Stage?
        get() = table.scene.window as Stage?

    private fun update() {
        table.items = FXCollections.observableList(picket.modelData)
        table.refresh()
    }

    @FXML
    private fun onEditCommit(event: TableColumn.CellEditEvent<ModelLayer, Double>) {
        val index: Int = event.tablePosition.row
        val oldValue: ModelLayer = event.rowValue
        val newInputValue: Double = event.newValue
        val newValue: ModelLayer = when (event.tableColumn) {
            powerCol -> oldValue.withPower(newInputValue)
            resistanceCol -> oldValue.withResistance(newInputValue)
            else -> throw RuntimeException("Something went wrong!")
        }
        if (!event.newValue.isNaN()) {
            updateIfValidElseAlert(picket.modelData.toMutableList().also { it[index] = newValue })
        } else {
            table.refresh()
        }
    }

    @FXML
    private fun addLayer() {
        if (!addBtn.isDisable) {
            val newResistanceValue: Double = try {
                decimalFormat.parse(resistanceTextField.text).toDouble()
            } catch (_: ParseException) {
                return
            }
            val newPowerValue: Double = try {
                decimalFormat.parse(powerTextField.text).toDouble()
            } catch (_: ParseException) {
                return
            }
            val index = try {
                indexTextField.text.toInt().coerceAtMost(picket.modelData.lastIndex + 1)
            } catch (_: NumberFormatException) {
                picket.modelData.lastIndex + 1
            }
            updateIfValidElseAlert(
                picket.modelData.toMutableList().apply {
                    add(index, ModelLayer.create(newPowerValue, newResistanceValue))
                }
            )
        }
    }

    @FXML
    private fun deleteSelected() {
        picket.modelData.toMutableList().apply {
            removeAllAt(table.selectionModel.selectedIndices)
        }.also {
            updateIfValidElseAlert(it)
        }
    }

    private fun updateIfValidElseAlert(newModelData: List<ModelLayer>) {
        val modified = picket.withModelData(newModelData)
        val violations = validator.validate(modified)
        if (violations.isNotEmpty()) {
            alertsFactory.violationsAlert(violations, stage).show()
            table.refresh()
        } else {
            historyManager.snapshotAfter { sectionManager.update(modified) }
            FXUtils.unfocus(indexTextField, powerTextField, resistanceTextField)
        }
    }

    @FXML
    private fun importModel() = mainViewControllerProvider.get().importMOD()

    @FXML
    private fun dragOverHandle(dragEvent: DragEvent) {
        if (dragEvent.dragboard.hasFiles()) {
            val files = dragEvent.dragboard.files
            for (file in files) {
                if (file.name.endsWith(".MOD") || file.name.endsWith(".mod")) {
                    dragEvent.acceptTransferModes(*TransferMode.COPY_OR_MOVE)
                }
            }
        }
        dragEvent.consume()
    }

    @FXML
    private fun dragDropHandle(dragEvent: DragEvent) {
        if (dragEvent.dragboard.hasFiles()) {
            val files = dragEvent.dragboard.files
            dragEvent.isDropCompleted = true
            dragEvent.consume()
            for (file in files) {
                if (file.name.endsWith(".MOD", ignoreCase = true)) {
                    mainViewControllerProvider.get().importMOD(file)
                }
            }
        }
    }
}