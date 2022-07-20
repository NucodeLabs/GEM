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
import javafx.scene.input.Clipboard
import javafx.scene.input.DataFormat
import javafx.stage.Stage
import javafx.util.Callback
import javafx.util.StringConverter
import ru.nucodelabs.data.fx.ObservableExperimentalData
import ru.nucodelabs.data.fx.ObservableSection
import ru.nucodelabs.data.fx.toObservable
import ru.nucodelabs.data.ves.*
import ru.nucodelabs.gem.app.snapshot.HistoryManager
import ru.nucodelabs.gem.extensions.fx.bidirectionalNot
import ru.nucodelabs.gem.extensions.fx.getValue
import ru.nucodelabs.gem.extensions.fx.toObservableList
import ru.nucodelabs.gem.extensions.std.toDoubleOrNullBy
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.AlertsFactory
import ru.nucodelabs.gem.view.main.FileImporter
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class ExperimentalTableController @Inject constructor(
    private val picketObservable: ObservableObjectValue<Picket>,
    private val validator: Validator,
    private val observableSection: ObservableSection,
    private val picketIndexProperty: IntegerProperty,
    private val historyManager: HistoryManager<Section>,
    private val alertsFactory: AlertsFactory,
    private val doubleStringConverter: StringConverter<Double>,
    private val decimalFormat: DecimalFormat,
    fileImporterProvider: Provider<FileImporter>
) : AbstractController(), FileImporter by fileImporterProvider.get() {

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
            newScene.windowProperty().addListener { _, _, newStage ->
                newStage.focusedProperty().addListener { _, _, isFocused ->
                    if (isFocused) {
                        pasteBtn.isDisable = !Clipboard.getSystemClipboard().hasContent(DataFormat.PLAIN_TEXT)
                    }
                }
            }
        }

        setupCellFactories()
        setupRowFactory()
        setupAutoRefreshTable()
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
                table.selectionModel.selectedItems.forEach { it.isHidden = isHidden }
                commitChanges()
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
        val parser = TextToTableParser(Clipboard.getSystemClipboard().string)
        try {
            val a = "abcdefghijklmnopqrstuvwxyz".uppercase().toCharArray()
            val pastedItems: List<ExperimentalData> = when (parser.columnsCount) {
                3 -> parser.parsedTable.mapIndexed { i, row ->
                    val ab2 = row[0]?.replace(',', '.')?.toDoubleOrNullBy(decimalFormat)
                        ?: throw IllegalStateException("${a[0]}${i + 1} - Ожидалось AB/2, было ${row[0]}")
                    val mn2 = row[1]?.replace(',', '.')?.toDoubleOrNullBy(decimalFormat)
                        ?: throw IllegalStateException("${a[1]}${i + 1} - Ожидалось MN/2, было ${row[1]}")
                    val resApp = row[2]?.replace(',', '.')?.toDoubleOrNullBy(decimalFormat)
                        ?: throw IllegalStateException("${a[2]}${i + 1}} - Ожидалось ρₐ, было ${row[2]}")
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
                4 -> parser.parsedTable.mapIndexed { i, row ->
                    ExperimentalData(
                        ab2 = row[0]?.replace(',', '.')?.toDoubleOrNullBy(decimalFormat)
                            ?: throw IllegalStateException("${a[0]}${i + 1} - Ожидалось AB/2, было ${row[0]}"),
                        mn2 = row[1]?.replace(',', '.')?.toDoubleOrNullBy(decimalFormat)
                            ?: throw IllegalStateException("${a[1]}${i + 1} - Ожидалось MN/2, было ${row[1]}"),
                        voltage = row[2]?.replace(',', '.')?.toDoubleOrNullBy(decimalFormat)
                            ?: throw IllegalStateException("${a[2]}${i + 1} - Ожидалось U, было ${row[2]}"),
                        amperage = row[3]?.replace(',', '.')?.toDoubleOrNullBy(decimalFormat)
                            ?: throw IllegalStateException("${a[3]}${i + 1} - Ожидалось I, было ${row[3]}")
                    )
                }
                5 -> parser.parsedTable.mapIndexed { i, row ->
                    ExperimentalData(
                        ab2 = row[0]?.replace(',', '.')?.toDoubleOrNullBy(decimalFormat)
                            ?: throw IllegalStateException("${a[0]}${i + 1} - Ожидалось AB/2, было ${row[0]}"),
                        mn2 = row[1]?.replace(',', '.')?.toDoubleOrNullBy(decimalFormat)
                            ?: throw IllegalStateException("${a[1]}${i + 1} - Ожидалось MN/2, было ${row[1]}"),
                        voltage = row[2]?.replace(',', '.')?.toDoubleOrNullBy(decimalFormat)
                            ?: throw IllegalStateException("${a[2]}${i + 1} - Ожидалось U, было ${row[2]}"),
                        amperage = row[3]?.replace(',', '.')?.toDoubleOrNullBy(decimalFormat)
                            ?: throw IllegalStateException("${a[3]}${i + 1} - Ожидалось I, было ${row[3]}"),
                        resistanceApparent = row[4]?.replace(',', '.')?.toDoubleOrNullBy(decimalFormat)
                            ?: throw IllegalStateException("${a[4]}${i + 1} - Ожидалось ρₐ, было ${row[4]}")
                    )
                }
                else -> throw IllegalStateException(
                    """
                    Допустимые числа колонок: 3, 4, 5.
                    Соответствующий порядок:
                    3 колонки - AB/2, MN/2 и ρₐ
                        Берется I = 100mA, U выражается из ρₐ, I, K
                        
                    4 колонки - AB/2, MN/2, U, I
                        ρₐ рассчитывается по формуле.
                        
                    5 колонок - AB/2, MN/2, U, I, ρₐ
                    
                    Погрешность берется δρₐ = 5%
                    """.trimIndent()
                )
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