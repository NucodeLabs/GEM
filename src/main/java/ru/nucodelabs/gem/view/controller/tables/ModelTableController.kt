package ru.nucodelabs.gem.view.controller.tables

import jakarta.validation.Validator
import javafx.beans.binding.Bindings.createBooleanBinding
import javafx.beans.binding.Bindings.createStringBinding
import javafx.beans.property.IntegerProperty
import javafx.beans.value.ObservableObjectValue
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.input.*
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.util.Callback
import javafx.util.StringConverter
import ru.nucodelabs.gem.app.snapshot.HistoryManager
import ru.nucodelabs.gem.fxmodel.ves.ObservableModelLayer
import ru.nucodelabs.gem.fxmodel.ves.ObservableSection
import ru.nucodelabs.gem.fxmodel.ves.app.VesFxAppModel
import ru.nucodelabs.gem.fxmodel.ves.mapper.VesFxModelMapper
import ru.nucodelabs.gem.util.TextToTableParser
import ru.nucodelabs.gem.util.fx.getValue
import ru.nucodelabs.gem.util.fx.toObservableList
import ru.nucodelabs.gem.util.std.toDoubleOrNullBy
import ru.nucodelabs.gem.view.AlertsFactory
import ru.nucodelabs.gem.view.controller.AbstractController
import ru.nucodelabs.gem.view.controller.FileImporter
import ru.nucodelabs.gem.view.controller.main.InitialModelConfigurationViewController
import ru.nucodelabs.gem.view.controller.util.DEFAULT_FONT_SIZE
import ru.nucodelabs.gem.view.controller.util.indexCellFactory
import ru.nucodelabs.geo.ves.ModelLayer
import ru.nucodelabs.geo.ves.Picket
import ru.nucodelabs.geo.ves.Section
import ru.nucodelabs.geo.ves.calc.divide
import ru.nucodelabs.geo.ves.calc.join
import ru.nucodelabs.geo.ves.calc.zOfModelLayers
import ru.nucodelabs.geo.ves.toTabulatedTable
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

const val STYLE_FOR_FIXED = """
    -fx-text-fill: White;
    -fx-background-color: Gray;
"""

@JvmField
val MOD_HELP_PASTE = """
    Допустимые числа колонок: 2.
    Соответствующий порядок:
    2 колонки - Мощность H, сопротивление ρ
    """.trimIndent()

class ModelTableController @Inject constructor(
    private val picketObservable: ObservableObjectValue<Picket?>,
    private val fileImporterProvider: Provider<FileImporter>,
    private val alertsFactory: AlertsFactory,
    private val observableSection: ObservableSection,
    private val picketIndexProperty: IntegerProperty,
    private val validator: Validator,
    private val historyManager: HistoryManager<Section>,
    private val doubleStringConverter: StringConverter<Double>,
    private val decimalFormat: DecimalFormat,
    private val mapper: VesFxModelMapper,
    private val appModel: VesFxAppModel
) : AbstractController(), FileImporter by fileImporterProvider.get() {

    @FXML
    private lateinit var copyFromRightBtn: Button

    @FXML
    private lateinit var copyFromLeftBtn: Button

    @FXML
    private lateinit var zCol: TableColumn<ObservableModelLayer, Double>

    @FXML
    private lateinit var indexCol: TableColumn<Any, Int>

    @FXML
    private lateinit var powerCol: TableColumn<ObservableModelLayer, Double>

    @FXML
    private lateinit var resistanceCol: TableColumn<ObservableModelLayer, Double>

    @FXML
    private lateinit var table: TableView<ObservableModelLayer>

    @FXML
    private lateinit var initialModelConfigurationView: VBox

    @FXML
    private lateinit var initialModelConfigurationViewController: InitialModelConfigurationViewController

    override val stage: Stage?
        get() = table.scene.window as Stage?


    private val picket: Picket
        get() = picketObservable.get()!!

    private val picketIndex by picketIndexProperty

    override fun initialize(location: URL, resources: ResourceBundle) {
        picketObservable.addListener { _, oldValue, newValue ->
            newValue?.let {
                if (oldValue != null
                    && newValue.modelData != table.items.map { mapper.toModel(it) }
                ) {
                    update()
                } else if (oldValue == null) {
                    update()
                }
            }
        }

        table.itemsProperty().addListener { _, _, _ -> listenToItemsList() }

        table.selectionModel.selectionMode = SelectionMode.MULTIPLE

        table.addEventHandler(KeyEvent.KEY_PRESSED) { e ->
            when (e.code) {
                KeyCode.DELETE, KeyCode.BACK_SPACE -> deleteSelected()
                KeyCode.C -> if (e.isShortcutDown) copySelected()
                else -> {}
            }
        }

        setupCellFactories()
        setupRowFactory()
        setupButtons()
        setupAutoRefreshTable()
    }

    private fun setupAutoRefreshTable() {
        table.itemsProperty().addListener { _, _, newValue: ObservableList<ObservableModelLayer> ->
            newValue.addListener(ListChangeListener {
                while (it.next()) {
                    table.refresh()
                }
            })
            table.refresh()
        }
    }

    private fun fixPowerForSelected() {
        val modelData = table.items.map { mapper.toModel(it) }.toMutableList()
        for (index in table.selectionModel.selectedIndices) {
            modelData[index] = modelData[index].copy(isFixedPower = true)
        }
        table.items.setAll(modelData.map { mapper.toObservable(it) })
    }

    private fun fixResistanceForSelected() {
        val modelData = picket.modelData.toMutableList()
        for (index in table.selectionModel.selectedIndices) {
            modelData[index] = modelData[index].copy(isFixedResistance = true)
        }
        table.items.setAll(modelData.map { mapper.toObservable(it) })
    }

    private fun unfixPowerForSelected() {
        val modelData = picket.modelData.toMutableList()
        for (index in table.selectionModel.selectedIndices) {
            modelData[index] = modelData[index].copy(isFixedPower = false)
        }
        table.items.setAll(modelData.map { mapper.toObservable(it) })
    }

    private fun unfixResistanceForSelected() {
        val modelData = picket.modelData.toMutableList()
        for (index in table.selectionModel.selectedIndices) {
            modelData[index] = modelData[index].copy(isFixedResistance = false)
        }
        table.items.setAll(modelData.map { mapper.toObservable(it) })
    }

    private fun setupRowFactory() {
        table.rowFactory = Callback {
            TableRow<ObservableModelLayer>().apply {
                val createContextMenu = {
                    ContextMenu(
                        MenuItem("Удалить").apply {
                            onAction = EventHandler { deleteSelected() }
                        },
                        MenuItem("Копировать в буфер обмена").apply {
                            onAction = EventHandler { copySelected() }
                        },
                        MenuItem("Разделить").apply {
                            onAction = EventHandler { divideSelected() }
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
                                MenuItem("Объединить").apply {
                                    onAction = EventHandler { joinSelected() }
                                },
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
                        style = "-fx-font-size: $DEFAULT_FONT_SIZE;"
                    }
                }

                onContextMenuRequested = EventHandler { createContextMenu().show(this, it.screenX, it.screenY) }
            }
        }
    }

    private fun joinSelected() {
        val selected = table.selectionModel.selectedItems.map { mapper.toModel(it) }
        val joined = selected.join()
        val items = table.items.map { mapper.toModel(it) }.toMutableList()
        items -= selected.toSet()
        items.add(table.selectionModel.selectedIndices[0], joined)
        table.items.setAll(items.map { mapper.toObservable(it) })
    }

    private fun copySelected() {
        Clipboard.getSystemClipboard().setContent(
            buildMap {
                put(
                    DataFormat.PLAIN_TEXT,
                    table.selectionModel.selectedItems.map { mapper.toModel(it) }.toTabulatedTable()
                )
            }
        )
    }

    private fun divideSelected() {
        val modelData = picket.modelData.toMutableList()
        for (index in table.selectionModel.selectedIndices) {
            if (modelData.size == 1) {
                modelData.add(0, modelData[index].copy(power = 10.0))
            } else {
                if (index == modelData.lastIndex) {
                    modelData.add(index, modelData[index - 1].copy(resistance = modelData.last().resistance))
                } else {
                    val (fst, snd) = modelData[index].divide()
                    modelData[index] = fst
                    modelData.add(index + 1, snd)
                }
            }
        }
        table.items.setAll(modelData.map { mapper.toObservable(it) })
    }

    private fun setupCellFactories() {
        indexCol.cellFactory = indexCellFactory()
        powerCol.cellValueFactory = Callback { features -> features.value.powerProperty().asObject() }
        resistanceCol.cellValueFactory = Callback { features -> features.value.resistanceProperty().asObject() }
        zCol.cellFactory = Callback {
            TableCell<ObservableModelLayer, Double>().apply {
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

        val editableColumns = listOf(
            powerCol,
            resistanceCol
        )
        editableColumns.forEach {
            it.cellFactory = Callback { col ->
                TextFieldTableCell.forTableColumn<ObservableModelLayer, Double>(doubleStringConverter).call(col).apply {
                    when (col) {
                        powerCol -> indexProperty().addListener { _, _, _ ->
                            if (index >= 0 && index <= picket.modelData.lastIndex) {
                                style = if (picket.modelData[index].isFixedPower) {
                                    STYLE_FOR_FIXED
                                } else {
                                    ""
                                }
                            }
                        }

                        resistanceCol -> indexProperty().addListener { _, _, _ ->
                            if (index >= 0 && index <= picket.modelData.lastIndex) {
                                style = if (picket.modelData[index].isFixedResistance) {
                                    STYLE_FOR_FIXED
                                } else {
                                    ""
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private fun setupButtons() {
        copyFromLeftBtn.disableProperty().bind(
            createBooleanBinding(
                { observableSection.pickets.size <= 1 || picketIndex == 0 },
                observableSection.pickets, picketIndexProperty
            )
        )
        copyFromRightBtn.disableProperty().bind(
            createBooleanBinding(
                { observableSection.pickets.size <= 1 || picketIndex == observableSection.pickets.lastIndex },
                observableSection.pickets, picketIndexProperty
            )
        )
    }

    private fun update() {
        mapItems()
        listenToItemsProperties(table.items)
        listenToItemsList()
        table.refresh()
    }

    private fun mapItems() {
        table.items = picket.modelData.map { mapper.toObservable(it) }.toObservableList()
    }

    private fun listenToItemsList() {
        table.items.addListener(ListChangeListener { c ->
            while (c.next()) {
                when {
                    c.wasAdded() -> {
                        listenToItemsProperties(c.addedSubList)
                        commitChanges()
                    }

                    c.wasRemoved() -> commitChanges()
                    c.wasPermutated() -> commitChanges()
                }
            }
        })
    }

    private fun listenToItemsProperties(items: List<ObservableModelLayer>) {
        items.forEach { layer ->
            layer.powerProperty().addListener { _, oldPow, newPow ->
                val violations = validator.validateValue(ModelLayer::class.java, "power", newPow)
                if (violations.isEmpty()) {
                    commitChanges()
                    update()
                } else {
                    layer.power = oldPow.toDouble()
                    alertsFactory.violationsAlert(violations, stage).show()
                }
            }
            layer.resistanceProperty().addListener { _, oldRes, newRes ->
                val violations = validator.validateValue(ModelLayer::class.java, "resistance", newRes)
                if (violations.isEmpty()) {
                    commitChanges()
                } else {
                    layer.resistance = oldRes.toDouble()
                    alertsFactory.violationsAlert(violations, stage).show()
                }
            }
            layer.fixedPowerProperty().addListener { _, _, _ -> commitChanges() }
            layer.fixedResistanceProperty().addListener { _, _, _ -> commitChanges() }
        }
    }

    private fun commitChanges() {
        val modelDataInTable = table.items.map { mapper.toModel(it) }
        if (modelDataInTable != picket.modelData) {
            historyManager.snapshotAfter {
                observableSection.pickets[picketIndex] = picket.copy(modelData = modelDataInTable)
            }
        }
    }

    @FXML
    private fun deleteSelected() {
        table.items.removeAll(table.selectionModel.selectedItems)
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
    fun simpleInitialModel() {
        appModel.applySimpleInitialModel()
    }

    @FXML
    fun multiLayerInitialModel() {
        appModel.applyArbitraryInitialModel(initialModelConfigurationViewController.parameters)
    }

    @FXML
    private fun copyFromLeft() {
        table.items.setAll(observableSection.pickets[picketIndex - 1].modelData.map { mapper.toObservable(it) })
    }

    @FXML
    private fun copyFromRight() {
        table.items.setAll(observableSection.pickets[picketIndex + 1].modelData.map { mapper.toObservable(it) })
    }

    @FXML
    private fun pasteFromClipboard() {
        val text = Clipboard.getSystemClipboard().string
        val parser = if (text != null) TextToTableParser(text) else return
        try {
            val a = "abcdefghijklmnopqrstuvwxyz".uppercase().toCharArray()
            val parsedTable = parser.parsedTable.filter { row -> row.none { it == null } }

            fun String?.process(expected: String, row: Int, col: Int) =
                this?.replace(',', '.')?.toDoubleOrNullBy(decimalFormat)
                    ?: throw IllegalArgumentException("${a[col]}${row + 1} - Ожидалось $expected, было $this")

            val pastedItems: List<ModelLayer> = when (parser.columnsCount) {
                2 -> parsedTable.mapIndexed { rowIdx, row ->
                    val pow = row[0].process("H", rowIdx, 0)
                    val res = row[1].process("ρ", rowIdx, 1)
                    ModelLayer(
                        resistance = res,
                        power = pow
                    )
                }

                else -> {
                    val errorRow = parsedTable.find { row -> row.count { it != null } != 2 }
                    val errorRowIdx = parsedTable.indexOf(errorRow)
                    throw IllegalStateException(
                        """
                    Строка ${errorRowIdx + 1}: ${errorRow?.asList()}
                    
                    $MOD_HELP_PASTE
                    """.trimIndent()
                    )
                }
            }
            for (item in pastedItems) {
                val violations = validator.validate(item)
                if (violations.isNotEmpty()) {
                    alertsFactory.violationsAlert(violations, stage).show()
                    return
                }
            }
            table.items += pastedItems.map { mapper.toObservable(it) }
        } catch (e: Exception) {
            alertsFactory.simpleExceptionAlert(e, stage).show()
        }
    }
}