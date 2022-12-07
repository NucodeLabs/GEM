package ru.nucodelabs.gem.view.main

import javafx.beans.binding.Bindings.createStringBinding
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ObservableObjectValue
import javafx.collections.ListChangeListener
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.chart.LineChart
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.stage.Stage
import javafx.util.Callback
import ru.nucodelabs.algorithms.normalization.FixableValue
import ru.nucodelabs.algorithms.normalization.distinctMn2
import ru.nucodelabs.algorithms.normalization.normalizeExperimentalData
import ru.nucodelabs.gem.fxmodel.ObservableSection
import ru.nucodelabs.geo.ves.Picket
import ru.nucodelabs.gem.util.fx.getValue
import ru.nucodelabs.gem.util.fx.setValue
import ru.nucodelabs.gem.util.fx.toObservableList
import ru.nucodelabs.gem.view.AbstractController
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

class NormalizationScreenController @Inject constructor(
    private val observableSection: ObservableSection,
    _picket: ObservableObjectValue<Picket>,
    private val decimalFormat: DecimalFormat
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
    private lateinit var mn2Col: TableColumn<FixedMn2Model, String>

    @FXML
    private lateinit var fixedCol: TableColumn<FixedMn2Model, Boolean>

    @FXML
    private lateinit var table: TableView<FixedMn2Model>

    @FXML
    private lateinit var chart: LineChart<Number, Number>

    @FXML
    private lateinit var root: Stage

    override val stage: Stage
        get() = root

    private val picket by _picket

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        observableSection.pickets.addListener(ListChangeListener { c ->
            while (c.next()) {
                if (root.isShowing) {
                    update()
                }
            }
        })

        root.onShown = EventHandler { update() }

        setupCellFactories()
    }

    private fun update() {
        mapItems()
        listenToItemsProperties()
        updateChart()
    }

    private fun setupCellFactories() {
        mn2Col.cellValueFactory =
            Callback { f -> createStringBinding({ decimalFormat.format(f.value.mn2) }, f.value.mn2Property()) }
        fixedCol.cellValueFactory = Callback { f -> f.value.fixedProperty() }
        fixedCol.cellFactory = CheckBoxTableCell.forTableColumn(fixedCol)
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
            ).mapIndexed { i, resApp ->
                XYChart.Data(picket.sortedExperimentalData[i].ab2 as Number, resApp as Number)
            }.toObservableList()
        )

        chart.data += series
        chart.data += normRes

        normRes.name = "Нормализованная кривая"
    }

    fun apply() {
        table.items[0].isFixed = true
    }
}