package ru.nucodelabs.gem.view.main

import javafx.beans.binding.Bindings.createStringBinding
import javafx.beans.property.*
import javafx.beans.value.ObservableObjectValue
import javafx.collections.ListChangeListener
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.chart.LineChart
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.*
import javafx.scene.control.cell.TextFieldTableCell
import javafx.stage.Stage
import javafx.util.Callback
import javafx.util.StringConverter
import ru.nucodelabs.algorithms.normalization.FixableValue
import ru.nucodelabs.algorithms.normalization.distinctMn2
import ru.nucodelabs.algorithms.normalization.normalizeExperimentalData
import ru.nucodelabs.gem.app.snapshot.HistoryManager
import ru.nucodelabs.gem.fxmodel.ObservableSection
import ru.nucodelabs.gem.util.fx.getValue
import ru.nucodelabs.gem.util.fx.setValue
import ru.nucodelabs.gem.util.fx.toObservableList
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.control.chart.log.LogarithmicAxis
import ru.nucodelabs.gem.view.tables.DEFAULT_FONT_SIZE
import ru.nucodelabs.gem.view.tables.STYLE_FOR_FIXED
import ru.nucodelabs.geo.ves.Picket
import ru.nucodelabs.geo.ves.Section
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

class NormalizationScreenController @Inject constructor(
    private val observableSection: ObservableSection,
    _picket: ObservableObjectValue<Picket>,
    private val decimalFormat: DecimalFormat,
    private val numberStringConverter: StringConverter<Number>,
    private val picketIndex: IntegerProperty,
    private val historyManager: HistoryManager<Section>
) : AbstractController() {

    private class FixedMn2Model(
        mn2: Double,
        fixed: Boolean
    ) {
        private val mn2Property = SimpleDoubleProperty(mn2)
        fun mn2Property(): DoubleProperty = mn2Property
        var mn2 by mn2Property

        private val fixedProperty = SimpleBooleanProperty(fixed)
        fun fixedProperty(): BooleanProperty = fixedProperty
        var isFixed by fixedProperty
    }

    @FXML
    private lateinit var xAxis: LogarithmicAxis

    @FXML
    private lateinit var yAxis: LogarithmicAxis

    @FXML
    private lateinit var mn2Col: TableColumn<FixedMn2Model, String>

    @FXML
    private lateinit var fixedCol: TableColumn<FixedMn2Model, Boolean>

    @FXML
    private lateinit var table: TableView<FixedMn2Model>

    @FXML
    private lateinit var chart: LineChart<Number, Number>

    @FXML
    private lateinit var root: Stage

    private lateinit var normalizationResult: List<Double>

    override val stage: Stage
        get() = root

    private val picket by _picket

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)

        xAxis.tickLabelFormatter = numberStringConverter
        yAxis.tickLabelFormatter = numberStringConverter

        observableSection.pickets.addListener(ListChangeListener { c ->
            while (c.next()) {
                if (root.isShowing) {
                    update()
                }
            }
        })

        root.onShown = EventHandler { update() }

        table.selectionModel.selectionMode = SelectionMode.MULTIPLE

        setupCellFactories()
        setupRowFactories()
    }

    private fun update() {
        mapItems()
        listenToItemsProperties()
        updateChart()
    }

    private fun setupCellFactories() {
        mn2Col.cellValueFactory =
            Callback { f -> createStringBinding({ decimalFormat.format(f.value.mn2) }, f.value.mn2Property()) }
        mn2Col.cellFactory =
            Callback {
                TextFieldTableCell<FixedMn2Model, String>().apply {
                    indexProperty().addListener { _, _, _ ->
                        if (index >= 0 && index <= table.items.lastIndex) {
                            style = if (table.items[index].isFixed) {
                                STYLE_FOR_FIXED
                            } else {
                                ""
                            }
                            table.items[index].fixedProperty().addListener { _, _, fix ->
                                style = if (fix) {
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

    private fun setupRowFactories() {
        table.rowFactory = Callback {
            TableRow<FixedMn2Model>().apply {
                val contextMenu = ContextMenu(
                    MenuItem("Зафиксировать").apply {
                        onAction = EventHandler { fixSelected() }
                    },
                    MenuItem("Разблокировать").apply {
                        onAction = EventHandler { unfixSelected() }
                    }
                ).apply { style = "-fx-font-size: $DEFAULT_FONT_SIZE;" }
                onContextMenuRequested = EventHandler { contextMenu.show(this, it.screenX, it.screenY) }
            }
        }
    }

    private fun unfixSelected() {
        table.selectionModel.selectedItems.forEach { it.isFixed = false }
    }

    private fun fixSelected() {
        table.selectionModel.selectedItems.forEach { it.isFixed = true }
    }

    private fun mapItems() {
        table.items.setAll(distinctMn2(picket.sortedExperimentalData).first.map { FixedMn2Model(it, false) })
    }

    private fun listenToItemsProperties() {
        table.items.forEach { item ->
            item.fixedProperty().addListener { _, _, _ ->
                updateChart()
            }
        }
    }

    private fun updateChart() {
        chart.data.clear()

        val (distValues, idxMap) = distinctMn2(picket.sortedExperimentalData)

        val series = distValues.mapIndexed { idx, mn2 ->
            Series(
                idxMap.mapIndexed { idxSrc, idxDist ->
                    idxSrc to idxDist
                }.filter { (_, idxDist) ->
                    idxDist == idx
                }.map { (idxSrc, _) ->
                    picket.sortedExperimentalData[idxSrc]
                }.map {
                    XYChart.Data(it.ab2 as Number, it.resistanceApparent as Number)
                }.toObservableList()
            ).also { it.name = decimalFormat.format(mn2) }
        }

        val normRes = Series(
            normalizeExperimentalData(
                picket.sortedExperimentalData,
                table.items.map { FixableValue(it.mn2, it.isFixed) },
                idxMap
            ).also {
                normalizationResult = it
            }.mapIndexed { i, resApp ->
                XYChart.Data(picket.sortedExperimentalData[i].ab2 as Number, resApp as Number)
            }.toObservableList()
        )

        chart.data += series
        chart.data += normRes

        normRes.name = "Нормализованная кривая"
    }

    fun apply() {
        val newExp = picket.sortedExperimentalData.mapIndexed { idx, exp ->
            exp.copy(resistanceApparent = normalizationResult[idx])
        }

        historyManager.snapshotAfter {
            observableSection.pickets[picketIndex.value] = picket.copy(experimentalData = newExp)
        }

        stage.close()
    }
}