package ru.nucodelabs.gem.view.tables

import com.google.inject.name.Named
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
import javafx.scene.input.Clipboard
import javafx.scene.input.DataFormat
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import javafx.util.Callback
import javafx.util.StringConverter
import ru.nucodelabs.gem.fxmodel.ObservableExperimentalData
import ru.nucodelabs.gem.fxmodel.ObservableSection
import ru.nucodelabs.gem.fxmodel.toObservable
import ru.nucodelabs.geo.ves.calc.*
import ru.nucodelabs.gem.app.snapshot.HistoryManager
import ru.nucodelabs.gem.util.fx.*
import ru.nucodelabs.gem.util.std.toDoubleOrNullBy
import ru.nucodelabs.gem.util.TextToTableParser
import ru.nucodelabs.gem.util.fx.DoubleValidationConverter
import ru.nucodelabs.gem.util.fx.decimalFilter
import ru.nucodelabs.gem.util.fx.toObservableList
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.AlertsFactory
import ru.nucodelabs.gem.view.main.CalculateErrorScreenController
import ru.nucodelabs.gem.view.main.FileImporter
import ru.nucodelabs.geo.ves.*
import ru.nucodelabs.geo.ves.calc.k
import ru.nucodelabs.geo.ves.calc.u
import ru.nucodelabs.geo.ves.calc.withCalculatedResistanceApparent
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

@JvmField
val EXP_HELP_PASTE = """
    Допустимые числа колонок: 3, 4, 5.
    Соответствующий порядок:
    3 колонки - AB/2, MN/2, ρₐ
        Берется I = 100mA, U выражается из ρₐ, I, K
                    
    4 колонки - AB/2, MN/2, U, I
        ρₐ рассчитывается по формуле
                    
    5 колонок - AB/2, MN/2, U, I, ρₐ
                
    Погрешность берется δρₐ = 5%
       """.trimIndent()

class ExperimentalTableController @Inject constructor(
    private val picketObservable: ObservableObjectValue<Picket>,
    private val validator: Validator,
    private val observableSection: ObservableSection,
    private val picketIndexProperty: IntegerProperty,
    private val historyManager: HistoryManager<Section>,
    private val alertsFactory: AlertsFactory,
    private val doubleStringConverter: StringConverter<Double>,
    private val decimalFormat: DecimalFormat,
    @Named("CSS") private val css: String,
    fileImporterProvider: Provider<FileImporter>
) : AbstractController(), FileImporter by fileImporterProvider.get() {

    @FXML
    private lateinit var calculateErrorScreen: Stage

    @FXML
    private lateinit var calculateErrorScreenController: CalculateErrorScreenController

    @FXML
    private lateinit var pasteBtn: Button

    @FXML
    private lateinit var isHiddenCol: TableColumn<ObservableExperimentalData, Boolean>

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
    private lateinit var table: TableView<ObservableExperimentalData>

    override val stage: Stage
        get() = table.scene.window as Stage

    private val picket: Picket
        get() = picketObservable.get()!!

    private val picketIndex by picketIndexProperty

    override fun initialize(location: URL, resources: ResourceBundle) {
        picketObservable.addListener { _, oldValue: Picket?, newValue: Picket? ->
            if (newValue != null) {
                if (oldValue != null
                    && newValue.sortedExperimentalData != table.items.map { it.toExperimentalData() }
                ) {
                    update()
                } else if (oldValue == null) {
                    update()
                }
            }
        }

        table.itemsProperty().addListener { _, _, _ -> listenToItemsList() }

        table.selectionModel.selectionMode = SelectionMode.MULTIPLE

        table.sceneProperty().addListener { _, _, newScene ->
            newScene?.windowProperty()?.addListener { _, _, newStage ->
                newStage?.focusedProperty()?.addListener { _, _, isFocused ->
                    if (isFocused) {
                        pasteBtn.isDisable = !Clipboard.getSystemClipboard().hasContent(DataFormat.PLAIN_TEXT)
                    }
                }
            }
        }

        table.addEventHandler(KeyEvent.KEY_PRESSED) { e ->
            when (e.code) {
                KeyCode.DELETE, KeyCode.BACK_SPACE -> deleteSelected()
                KeyCode.C -> if (e.isShortcutDown) copySelected()
                else -> {}
            }
        }

        setupCellFactories()
        setupRowFactory()
        setupAutoRefreshTable()
    }

    private fun copySelected() {
        Clipboard.getSystemClipboard().setContent(
            buildMap {
                put(
                    DataFormat.PLAIN_TEXT,
                    table.selectionModel.selectedItems.map { it.toExperimentalData() }.toTabulatedTable()
                )
            }
        )
    }

    private fun setupAutoRefreshTable() {
        table.itemsProperty().addListener { _, _, newValue: ObservableList<ObservableExperimentalData> ->
            newValue.addListener(ListChangeListener {
                while (it.next()) {
                    table.refresh()
                }
            })
            table.refresh()
        }
    }

    private fun setupCellFactories() {
        indexCol.cellFactory = indexCellFactory()

        isHiddenCol.cellValueFactory = Callback { features -> features.value.hiddenProperty().bidirectionalNot() }
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

    private fun setupRowFactory() {
        table.rowFactory = Callback { _ ->
            TableRow<ObservableExperimentalData>().apply {
                val contextMenu = ContextMenu(
                    MenuItem("Удалить").apply {
                        onAction = EventHandler { deleteSelected() }
                    },
                    MenuItem("Копировать в буфер обмена").apply {
                        onAction = EventHandler { copySelected() }
                    },
                    MenuItem("Рассчитать ρₐ").apply {
                        onAction = EventHandler { recalculateSelected() }
                    },
                    MenuItem("Установить погрешность").apply {
                        val dialog = TextInputDialog().apply {
                            initOwner(stage)
                            stylesheets += css
                            headerText = "Введите значение погрешности [%]"
                            contentText = "δρₐ = "
                            editor.also { tf ->
                                tf.textFormatter = TextFormatter(
                                    DoubleValidationConverter(decimalFormat) { value ->
                                        val violations = validator.validateValue(
                                            ExperimentalData::class.java,
                                            "errorResistanceApparent",
                                            value
                                        )
                                        violations.isEmpty().also { valid ->
                                            if (!valid) {
                                                alertsFactory.violationsAlert(violations, stage).show()
                                            }
                                        }
                                    },
                                    5.0,
                                    decimalFilter(decimalFormat)
                                )
                            }
                        }
                        onAction = EventHandler {
                            val result = dialog.showAndWait()
                            if (result.isPresent) {
                                setErrorOnSelected(dialog.editor.textFormatter.value as Double)
                            }
                        }
                    },
                    MenuItem("Рассчитать погрешность").apply {
                        onAction = EventHandler { showCalcErrorWindowForSelected() }
                    },
                ).apply {
                    style = "-fx-font-size: $DEFAULT_FONT_SIZE;"
                }

                onContextMenuRequested = EventHandler { contextMenu.show(this, it.screenX, it.screenY) }
            }
        }
    }

    private fun update() {
        mapItems()
        listenToItemsProperties(table.items)
        listenToItemsList()
        table.refresh()
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

    private fun mapItems() {
        table.items = picket.sortedExperimentalData.map { it.toObservable() }.toObservableList()
    }

    private fun listenToItemsProperties(items: List<ObservableExperimentalData>) {
        items.forEach { expData ->
            expData.ab2Property().addListener { _, oldAb2, newAb2 ->
                val violations = validator.validateValue(ExperimentalData::class.java, "ab2", newAb2)
                if (violations.isEmpty()) {
                    commitChanges()
                } else {
                    expData.ab2 = oldAb2.toDouble()
                    alertsFactory.violationsAlert(violations, stage).show()
                }
            }
            expData.mn2Property().addListener { _, oldMn2, newMn2 ->
                val violations = validator.validateValue(ExperimentalData::class.java, "mn2", newMn2)
                if (violations.isEmpty()) {
                    commitChanges()
                } else {
                    expData.mn2 = oldMn2.toDouble()
                    alertsFactory.violationsAlert(violations, stage).show()
                }
            }
            expData.errorResistanceApparentProperty().addListener { _, oldErr, newErr ->
                val violations = validator.validateValue(
                    ExperimentalData::class.java,
                    "errorResistanceApparent",
                    newErr
                )
                if (violations.isEmpty()
                ) {
                    commitChanges()
                } else {
                    expData.errorResistanceApparent = oldErr.toDouble()
                    alertsFactory.violationsAlert(violations, stage).show()
                }
            }
            expData.amperageProperty().addListener { _, oldAmp, newAmp ->
                val violations = validator.validateValue(ExperimentalData::class.java, "amperage", newAmp)
                if (violations.isEmpty()) {
                    commitChanges()
                } else {
                    expData.amperage = oldAmp.toDouble()
                    alertsFactory.violationsAlert(violations, stage).show()
                }
            }
            expData.voltageProperty().addListener { _, oldVolt, newVolt ->
                val violations = validator.validateValue(ExperimentalData::class.java, "voltage", newVolt)
                if (violations.isEmpty()) {
                    commitChanges()
                } else {
                    expData.voltage = oldVolt.toDouble()
                    alertsFactory.violationsAlert(violations, stage).show()
                }
            }
            expData.resistanceApparentProperty().addListener { _, oldRes, newRes ->
                val violations = validator.validateValue(
                    ExperimentalData::class.java,
                    "resistanceApparent",
                    newRes
                )
                if (violations.isEmpty()
                ) {
                    commitChanges()
                } else {
                    expData.resistanceApparent = oldRes.toDouble()
                    alertsFactory.violationsAlert(violations, stage).show()
                }
            }
            expData.hiddenProperty().addListener { _, _, isHidden ->
                if (table.selectionModel.selectedItems.isEmpty()) {
                    toggleSingleHidden(expData, isHidden)
                }
                if (expData in table.selectionModel.selectedItems) {
                    if (table.selectionModel.selectedItems.size == 1) {
                        toggleSingleHidden(expData, isHidden)
                    } else {
                        setIsHiddenOnSelected(isHidden)
                    }
                } else {
                    toggleSingleHidden(expData, isHidden)
                }
            }
        }
    }

    private fun commitChanges() {
        val experimentalDataInTable = table.items.map { it.toExperimentalData() }
        if (experimentalDataInTable != picket.sortedExperimentalData) {
            historyManager.snapshotAfter {
                observableSection.pickets[picketIndex] =
                    picket.copy(experimentalData = experimentalDataInTable)
            }
        }
    }

    private fun showCalcErrorWindowForSelected() {
        lazyInitCalcErrorScreen()
        calculateErrorScreenController.data.setAll(table.selectionModel.selectedItems.map { it.toExperimentalData() })
        calculateErrorScreen.show()
    }

    private fun lazyInitCalcErrorScreen() {
        if (calculateErrorScreen.owner == null) {
            calculateErrorScreen.initOwner(stage)
        }
        calculateErrorScreen.icons.setAll(stage.icons)
    }

    private fun setIsHiddenOnSelected(isHidden: Boolean) {
        val items = table.items.map { it.toExperimentalData() }.toMutableList()
        for (idx in table.selectionModel.selectedIndices) {
            items[idx] = items[idx].copy(isHidden = isHidden)
        }
        table.items.setAll(items.map { it.toObservable() })
    }

    private fun toggleSingleHidden(item: ObservableExperimentalData, isHidden: Boolean) {
        if (isHidden) {
            commitChanges()
        } else {
            val selected = item.toExperimentalData()
            val dupGroups = table.items.map { it.toExperimentalData() }.groupBy { it.ab2 }.values.filter { it.size > 1 }
            val group = dupGroups.find { selected in it }
            if (group != null) {
                val other = group - selected
                val all = table.items.map { it.toExperimentalData() }.toMutableList()
                all.replaceAll { if (it in other) it.copy(isHidden = true) else it }
                table.items.setAll(all.map { it.toObservable() })
            } else {
                commitChanges()
            }
        }
    }

    private fun setErrorOnSelected(error: Double) {
        val items = table.items.map { it.toExperimentalData() }.toMutableList()
        for (i in table.selectionModel.selectedIndices) {
            items[i] = items[i].copy(errorResistanceApparent = error)
        }
        table.items.setAll(items.map { it.toObservable() })
    }

    @FXML
    private fun deleteSelected() {
        table.items.removeAll(table.selectionModel.selectedItems)
    }

    @FXML
    private fun recalculateSelected() {
        val experimentalData = table.items.map { it.toExperimentalData() }.toMutableList()

        for (i in table.selectionModel.selectedIndices) {
            experimentalData[i] = experimentalData[i].withCalculatedResistanceApparent()
        }

        table.items.setAll(experimentalData.map { it.toObservable() })
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

            val pastedItems: List<ExperimentalData> = when (parser.columnsCount) {
                3 -> parsedTable.mapIndexed { rowIdx, row ->
                    val ab2 = row[0].process("AB/2", rowIdx, 0)
                    val mn2 = row[1].process("MN/2", rowIdx, 1)
                    val resApp = row[2].process("ρₐ", rowIdx, 2)
                    val amp = 100.0
                    val volt = u(resApp, 100.0, k(ab2, mn2))
                    ExperimentalData(
                        ab2 = ab2,
                        mn2 = mn2,
                        resistanceApparent = resApp,
                        amperage = amp,
                        voltage = volt
                    )
                }

                4 -> parsedTable.mapIndexed { rowIdx, row ->
                    ExperimentalData(
                        ab2 = row[0].process("AB/2", rowIdx, 0),
                        mn2 = row[1].process("MN/2", rowIdx, 1),
                        voltage = row[2].process("U", rowIdx, 2),
                        amperage = row[3].process("I", rowIdx, 3)
                    )
                }

                5 -> parsedTable.mapIndexed { rowIdx, row ->
                    ExperimentalData(
                        ab2 = row[0].process("AB/2", rowIdx, 0),
                        mn2 = row[1].process("MN/2", rowIdx, 1),
                        voltage = row[2].process("U", rowIdx, 2),
                        amperage = row[3].process("I", rowIdx, 3),
                        resistanceApparent = row[4].process("ρₐ", rowIdx, 4)
                    )
                }

                else -> {
                    val errorRow = parsedTable.find { row -> row.count { it != null } !in arrayOf(3, 4, 5) }
                    val errorRowIdx = parsedTable.indexOf(errorRow)
                    throw IllegalStateException(
                        """
                    Строка ${errorRowIdx + 1}: ${errorRow?.asList()}
                    
                    $EXP_HELP_PASTE
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
            table.items += pastedItems.map { it.toObservable() }
        } catch (e: Exception) {
            alertsFactory.simpleExceptionAlert(e, stage).show()
        }
    }
}