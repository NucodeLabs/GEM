package ru.nucodelabs.gem.view.tables

import jakarta.validation.Validator
import javafx.beans.binding.Bindings.createStringBinding
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableObjectValue
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import javafx.stage.Stage
import javafx.util.Callback
import javafx.util.StringConverter
import ru.nucodelabs.algorithms.primaryModel.PrimaryModel
import ru.nucodelabs.data.ves.ModelLayer
import ru.nucodelabs.data.ves.Picket
import ru.nucodelabs.data.ves.Section
import ru.nucodelabs.gem.app.model.SectionManager
import ru.nucodelabs.gem.app.snapshot.HistoryManager
import ru.nucodelabs.gem.extensions.fx.emptyBinding
import ru.nucodelabs.gem.utils.FXUtils
import ru.nucodelabs.gem.utils.FXUtils.isNotBlank
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.AlertsFactory
import ru.nucodelabs.gem.view.main.FileImporter
import java.net.URL
import java.text.DecimalFormat
import java.text.ParseException
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class ModelTableController @Inject constructor(
    private val picketObservable: ObservableObjectValue<Picket?>,
    private val fileImporterProvider: Provider<FileImporter>,
    private val alertsFactory: AlertsFactory,
    private val validator: Validator,
    private val sectionManager: SectionManager,
    private val historyManager: HistoryManager<Section>,
    private val doubleStringConverter: StringConverter<Double>,
    private val decimalFormat: DecimalFormat
) : AbstractController(), FileImporter by fileImporterProvider.get() {

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
        deleteBtn.disableProperty().bind(table.selectionModel.selectedItems.emptyBinding())

        setupCellFactories()
        setupValidation()
        setupRowFactory()

        table.itemsProperty().addListener { _, _, newValue: ObservableList<ModelLayer> ->
            newValue.addListener(ListChangeListener { table.refresh() })
            table.refresh()
        }
    }

    private fun fixPowerForSelected() {
        val modelData = picket.modelData.toMutableList()
        for (index in table.selectionModel.selectedIndices) {
            modelData[index] = modelData[index].withFixedPower(true)
        }
        updateIfValidElseAlert(modelData)
    }

    private fun fixResistanceForSelected() {
        val modelData = picket.modelData.toMutableList()
        for (index in table.selectionModel.selectedIndices) {
            modelData[index] = modelData[index].withFixedResistance(true)
        }
        updateIfValidElseAlert(modelData)
    }

    private fun unfixPowerForSelected() {
        val modelData = picket.modelData.toMutableList()
        for (index in table.selectionModel.selectedIndices) {
            modelData[index] = modelData[index].withFixedPower(false)
        }
        updateIfValidElseAlert(modelData)
    }

    private fun unfixResistanceForSelected() {
        val modelData = picket.modelData.toMutableList()
        for (index in table.selectionModel.selectedIndices) {
            modelData[index] = modelData[index].withFixedResistance(false)
        }
        updateIfValidElseAlert(modelData)
    }

    private fun setupRowFactory() {
        table.rowFactory = Callback {
            TableRow<ModelLayer>().apply {
                val createContextMenu = {
                    ContextMenu(
                        MenuItem("Удалить").apply {
                            onAction = EventHandler { deleteSelected() }
                        },
                    ).apply {
                        if (table.selectionModel.selectedItems.size == 1) {
                            items += MenuItem().apply {
                                if (item.isFixedResistance) {
                                    text = "Разблокировать сопротивление"
                                    onAction = EventHandler { unfixResistanceForSelected() }
                                } else {
                                    text = "Зафиксировать сопротивление"
                                    onAction = EventHandler { fixResistanceForSelected() }
                                }
                            }
                            items += MenuItem().apply {
                                if (item.isFixedPower) {
                                    text = "Разблокировать мощность"
                                    onAction = EventHandler { unfixPowerForSelected() }
                                } else {
                                    text = "Зафиксировать мощность"
                                    onAction = EventHandler { fixPowerForSelected() }
                                }
                            }
                        } else {
                            items += listOf(
                                MenuItem("Зафиксировать сопротивление").apply {
                                    onAction = EventHandler { fixResistanceForSelected() }
                                },
                                MenuItem("Разблокировать сопротивление").apply {
                                    onAction = EventHandler { unfixResistanceForSelected() }
                                },
                                MenuItem("Зафиксировать мощность").apply {
                                    onAction = EventHandler { fixPowerForSelected() }
                                },
                                MenuItem("Разблокировать мощность").apply {
                                    onAction = EventHandler { unfixPowerForSelected() }
                                }
                            )
                        }
                        style = "-fx-font-size: 14;"
                    }
                }

                onContextMenuRequested = EventHandler { createContextMenu().show(this, it.screenX, it.screenY) }
            }
        }
    }

    private fun setupCellFactories() {
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
    }

    private fun setupValidation() {
        val validInput = valid(indexTextField) { validateIndexInput(it) }
            .and(valid(resistanceTextField) { validateDoubleInput(it, decimalFormat) })
            .and(valid(powerTextField) { validateDoubleInput(it, decimalFormat) })

        val allRequiredNotBlank = isNotBlank(powerTextField.textProperty())
            .and(isNotBlank(resistanceTextField.textProperty()))

        addBtn.disableProperty().bind(validInput.not().or(allRequiredNotBlank.not()))
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
                    add(index, ModelLayer.createNotFixed(newPowerValue, newResistanceValue))
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
                    importMOD(file)
                }
            }
        }
    }

    @FXML
    fun makePrimaryModel() {
        val primaryModel = PrimaryModel(picket.experimentalData)
        val newModelData = primaryModel.get3LayersPrimaryModel()
        historyManager.snapshotAfter { sectionManager.update(picket.withModelData(newModelData)) }
    }
}