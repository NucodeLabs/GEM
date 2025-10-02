package ru.nucodelabs.gem.view.controller.tables

import jakarta.inject.Inject
import jakarta.inject.Provider
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
import javafx.util.Callback
import javafx.util.StringConverter
import ru.nucodelabs.gem.fxmodel.ves.ObservableModelLayer
import ru.nucodelabs.gem.fxmodel.ves.ObservableSection
import ru.nucodelabs.gem.fxmodel.ves.app.VesFxAppModel
import ru.nucodelabs.gem.fxmodel.ves.mapper.VesFxModelMapper
import ru.nucodelabs.gem.view.AlertsFactory
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
import ru.nucodelabs.kfx.core.AbstractViewController
import ru.nucodelabs.kfx.ext.get
import ru.nucodelabs.kfx.ext.toObservableList
import ru.nucodelabs.kfx.snapshot.HistoryManager
import ru.nucodelabs.util.Err
import ru.nucodelabs.util.Ok
import ru.nucodelabs.util.TextToTableParser
import ru.nucodelabs.util.toDoubleOrNullBy
import tornadofx.getValue
import java.net.URL
import java.text.DecimalFormat
import java.util.*

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
    private val historyManager: HistoryManager<Section>,
    private val converter: StringConverter<Number>,
    private val decimalFormat: DecimalFormat,
    private val mapper: VesFxModelMapper,
    private val appModel: VesFxAppModel,
    private val uiProps: ResourceBundle
) : AbstractViewController<VBox>(), FileImporter by fileImporterProvider.get() {

    @FXML
    private lateinit var copyFromRightBtn: Button

    @FXML
    private lateinit var copyFromLeftBtn: Button

    @FXML
    private lateinit var zCol: TableColumn<ObservableModelLayer, Number>

    @FXML
    private lateinit var indexCol: TableColumn<Any, Int>

    @FXML
    private lateinit var powerCol: TableColumn<ObservableModelLayer, Number>

    @FXML
    private lateinit var resCol: TableColumn<ObservableModelLayer, Number>

    @FXML
    private lateinit var table: TableView<ObservableModelLayer>

    @FXML
    private lateinit var initialModelConfigurationView: VBox

    @FXML
    private lateinit var initialModelConfigurationViewController: InitialModelConfigurationViewController

    private val picket: Picket
        get() = picketObservable.get()!!

    private val picketIndex by picketIndexProperty

    override fun initialize(location: URL, resources: ResourceBundle) {
        table.columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN
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
                else -> { /* ignore*/
                }
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

    private fun fixResistivityForSelected() {
        val modelData = picket.modelData.toMutableList()
        for (index in table.selectionModel.selectedIndices) {
            modelData[index] = modelData[index].copy(isFixedResistivity = true)
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

    private fun unfixResistivityForSelected() {
        val modelData = picket.modelData.toMutableList()
        for (index in table.selectionModel.selectedIndices) {
            modelData[index] = modelData[index].copy(isFixedResistivity = false)
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
                                if (item.isFixedResistivity) {
                                    text = "Разблокировать сопротивление"
                                    onAction = EventHandler { unfixResistivityForSelected() }
                                } else {
                                    text = "Зафиксировать сопротивление"
                                    onAction = EventHandler { fixResistivityForSelected() }
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
                                    onAction = EventHandler { fixResistivityForSelected() }
                                },
                                MenuItem("Разблокировать сопротивление").apply {
                                    onAction = EventHandler { unfixResistivityForSelected() }
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

                onContextMenuRequested = EventHandler {
                    if (item != null) createContextMenu().show(this, it.screenX, it.screenY)
                }
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
                    modelData.add(index, modelData[index - 1].copy(resistivity = modelData.last().resistivity))
                } else {
                    val (fst, snd) = modelData[index].divide()
                    modelData[index] = fst
                    modelData.add(index + 1, snd)
                }
            }
        }
        if (modelData.size > Picket.MAX_MODEL_DATA_SIZE) {
            alertsFactory.simpleAlert(
                text = "Количество слоев модели не должно превышать ${Picket.MAX_MODEL_DATA_SIZE}"
            ).show()
            return
        }
        table.items.setAll(modelData.map { mapper.toObservable(it) })
    }

    private fun setupCellFactories() {
        indexCol.cellFactory = indexCellFactory()
        powerCol.cellValueFactory = Callback { features -> features.value.powerProperty() }
        resCol.cellValueFactory = Callback { features -> features.value.resistivityProperty() }
        zCol.cellFactory = Callback {
            TableCell<ObservableModelLayer, Number>().apply {
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
            powerCol to ModelLayer::validatePower,
            resCol to ModelLayer::validateResistivity
        )
        editableColumns.forEach { (col, validate) ->
            col.cellFactory = Callback { _ ->
                TextFieldTableCell<ObservableModelLayer, Number>(converter).apply {
                    when (col) {
                        powerCol -> tableRowProperty()
                            .flatMap { it.itemProperty() }
                            .flatMap { it.fixedPowerProperty() }
                            .addListener { _, _, isFixed -> style = if (isFixed ?: false) STYLE_FOR_FIXED else "" }


                        resCol -> tableRowProperty()
                            .flatMap { it.itemProperty() }
                            .flatMap { it.fixedResistivityProperty() }
                            .addListener { _, _, isFixed -> style = if (isFixed ?: false) STYLE_FOR_FIXED else "" }

                    }

                }
            }

            val onEditCommitHandler = col.onEditCommit
            col.onEditCommit = EventHandler { event ->
                if (event.newValue == null) {
                    event.consume()
                    return@EventHandler
                }
                validate(event.newValue.toDouble())?.let { (prop, _) ->
                    alertsFactory.invalidInputAlert(uiProps["invalid.model.$prop"]).show()
                    event.consume()
                    return@EventHandler
                }
                onEditCommitHandler.handle(event)
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
            layer.powerProperty().addListener { _, oldPow, newPow -> commitChanges() }
            layer.resistivityProperty().addListener { _, oldRes, newRes -> commitChanges() }
            layer.fixedPowerProperty().addListener { _, _, _ -> commitChanges() }
            layer.fixedResistivityProperty().addListener { _, _, _ -> commitChanges() }
        }
    }

    private fun commitChanges() {
        val mappedModel = table.items.map { mapper.toModel(it) }
        if (mappedModel != picket.modelData) {
            val update = picket.copy(modelData = mappedModel)
            historyManager.snapshotAfter {
                observableSection.pickets[picketIndex] = update
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
                this?.replace(',', '.')
                    ?.toDoubleOrNullBy(decimalFormat)
                    ?: throw IllegalArgumentException("${a[col]}${row + 1} - Ожидалось $expected, было $this")

            val pastedItems = when (parser.columnsCount) {
                2 -> parsedTable.mapIndexed { rowIdx, row ->
                    val pow = row[0].process("H", rowIdx, 0)
                    val res = row[1].process("ρ", rowIdx, 1)
                    ModelLayer.new(
                        resistivity = res,
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
            val mapped = ArrayList<ObservableModelLayer>()
            for (item in pastedItems) {
                when (item) {
                    is Err -> {
                        alertsFactory.simpleAlert(text = item.error.joinToString())
                        return
                    }

                    is Ok -> mapped.add(mapper.toObservable(item.value))
                }
            }
            table.items += mapped
        } catch (e: Exception) {
            alertsFactory.simpleExceptionAlert(e, stage).show()
        }
    }
}