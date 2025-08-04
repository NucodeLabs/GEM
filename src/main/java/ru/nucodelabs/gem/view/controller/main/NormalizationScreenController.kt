package ru.nucodelabs.gem.view.controller.main

import jakarta.inject.Inject
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
import ru.nucodelabs.gem.app.snapshot.HistoryManager
import ru.nucodelabs.gem.fxmodel.ves.ObservableSection
import ru.nucodelabs.gem.util.fx.getValue
import ru.nucodelabs.gem.util.fx.setValue
import ru.nucodelabs.gem.util.fx.toObservableList
import ru.nucodelabs.gem.view.control.chart.log.LogarithmicAxis
import ru.nucodelabs.gem.view.controller.AbstractController
import ru.nucodelabs.gem.view.controller.tables.STYLE_FOR_FIXED
import ru.nucodelabs.gem.view.controller.util.DEFAULT_FONT_SIZE
import ru.nucodelabs.geo.ves.Picket
import ru.nucodelabs.geo.ves.Section
import ru.nucodelabs.geo.ves.calc.FixableValue
import ru.nucodelabs.geo.ves.calc.distinctMn2
import ru.nucodelabs.geo.ves.calc.normalizeExperimentalData
import java.math.MathContext
import java.math.RoundingMode
import java.net.URL
import java.text.DecimalFormat
import java.util.*

class NormalizationScreenController @Inject constructor(
    private val observableSection: ObservableSection,
    _picket: ObservableObjectValue<Picket>,
    private val decimalFormat: DecimalFormat,
    private val numberStringConverter: StringConverter<Number>,
    private val picketIndex: IntegerProperty,
    private val historyManager: HistoryManager<Section>
) : AbstractController() {

    private class Mn2Model(
        mn2: Double,
        fixed: Boolean,
        add: Double = .0
    ) {
        private val mn2Property = SimpleDoubleProperty(mn2)
        fun mn2Property(): DoubleProperty = mn2Property
        var mn2 by mn2Property

        private val fixedProperty = SimpleBooleanProperty(fixed)
        fun fixedProperty(): BooleanProperty = fixedProperty
        var isFixed by fixedProperty

        private val addProperty = SimpleDoubleProperty(add)
        fun addProperty(): DoubleProperty = addProperty
        var add by addProperty
    }

    @FXML
    private lateinit var addCol: TableColumn<Mn2Model, String>

    @FXML
    private lateinit var xAxis: LogarithmicAxis

    @FXML
    private lateinit var yAxis: LogarithmicAxis

    @FXML
    private lateinit var mn2Col: TableColumn<Mn2Model, String>

    @FXML
    private lateinit var mn2Table: TableView<Mn2Model>

    @FXML
    private lateinit var chart: LineChart<Number, Number>

    @FXML
    private lateinit var root: Stage

    override val stage: Stage
        get() = root

    private val picket by _picket

    private lateinit var normalizationResult: List<Double>
    private lateinit var additiveResult: List<Double>

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

        mn2Table.selectionModel.selectionMode = SelectionMode.MULTIPLE

        setupCellFactories()
        setupRowFactories()
    }

    private fun update() {
        mapItems()
        listenToItemsProperties()
        updateChartAndTable()
    }

    private fun setupCellFactories() {
        mn2Col.cellValueFactory =
            Callback { f -> createStringBinding({ decimalFormat.format(f.value.mn2) }, f.value.mn2Property()) }
        mn2Col.cellFactory =
            Callback {
                TextFieldTableCell<Mn2Model, String>().apply {
                    indexProperty().addListener { _, _, _ ->
                        if (index >= 0 && index <= mn2Table.items.lastIndex) {
                            style = if (mn2Table.items[index].isFixed) {
                                STYLE_FOR_FIXED
                            } else {
                                ""
                            }
                            mn2Table.items[index].fixedProperty().addListener { _, _, fix ->
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

        addCol.cellValueFactory =
            Callback { f ->
                createStringBinding(
                    {
                        f.value.add.toBigDecimal().round(MathContext(4, RoundingMode.UP)).toString()
                    },
                    f.value.addProperty()
                )
            }
        addCol.cellFactory = Callback { TextFieldTableCell() }
    }

    private fun setupRowFactories() {
        mn2Table.rowFactory = Callback {
            TableRow<Mn2Model>().apply {
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
        mn2Table.selectionModel.selectedItems.forEach { it.isFixed = false }
    }

    private fun fixSelected() {
        mn2Table.selectionModel.selectedItems.forEach { it.isFixed = true }
    }

    private fun mapItems() {
        mn2Table.items.setAll(distinctMn2(picket.sortedExperimentalData).first.map {
            Mn2Model(
                it,
                false
            )
        })
    }

    private fun listenToItemsProperties() {
        mn2Table.items.forEach { item ->
            item.fixedProperty().addListener { _, _, _ ->
                updateChartAndTable()
            }
        }
    }

    private fun updateChartAndTable() {
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

        val (normResApp, additive) = normalizeExperimentalData(
            picket.sortedExperimentalData,
            mn2Table.items.map { FixableValue(it.mn2, it.isFixed) },
            idxMap
        )

        additiveResult = additive
        normalizationResult = normResApp

        mn2Table.items.forEachIndexed { idx, mn2Model -> mn2Model.add = additive[idx] }
        mn2Table.refresh()

        val normRes = Series(
            normResApp.mapIndexed { i, resApp ->
                XYChart.Data(picket.sortedExperimentalData[i].ab2 as Number, resApp as Number)
            }.toObservableList()
        )

        chart.data += series
        chart.data += normRes

        normRes.name = "Нормализованная кривая"
    }

    @FXML
    private fun apply() {
        val newExp = picket.sortedExperimentalData.mapIndexed { idx, exp ->
            exp.copy(resistanceApparent = normalizationResult[idx])
        }

        historyManager.snapshotAfter {
            observableSection.pickets[picketIndex.value] = picket.copy(experimentalData = newExp)
        }

        stage.close()
    }
}