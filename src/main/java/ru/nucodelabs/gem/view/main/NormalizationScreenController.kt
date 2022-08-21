package ru.nucodelabs.gem.view.main

import javafx.beans.binding.Bindings.createStringBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableObjectValue
import javafx.collections.ListChangeListener
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.chart.LineChart
import javafx.scene.chart.XYChart.Data
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.stage.Stage
import javafx.util.Callback
import ru.nucodelabs.algorithms.normalization.FixableValue
import ru.nucodelabs.algorithms.normalization.normalizeExperimentalData
import ru.nucodelabs.data.fx.ObservableSection
import ru.nucodelabs.data.ves.Picket
import ru.nucodelabs.data.ves.distinctMn2
import ru.nucodelabs.gem.extensions.fx.getValue
import ru.nucodelabs.gem.extensions.fx.setValue
import ru.nucodelabs.gem.extensions.fx.toObservableList
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
        fun mn2Property() = mn2Property
        var mn2 by mn2Property

        private val fixedProperty = SimpleBooleanProperty(fixed)
        fun fixedProperty() = fixedProperty
        var fixed by fixedProperty
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
        table.items.setAll(distinctMn2(picket.sortedExperimentalData).keys.map { FixedMn2Model(it, false) })
    }

    private fun listenToItemsProperties() {
        table.items.forEach { item ->
            item.fixedProperty().addListener { _, _, _ -> updateChart() }
        }
    }

    private fun updateChart() {
        val map = distinctMn2(picket.sortedExperimentalData)

        val series = map.keys.map { mn2 ->
            Series(
                map[mn2]?.map { Data(it.ab2 as Number, it.resistanceApparent as Number) }?.toObservableList()
            ).also { it.name = decimalFormat.format(mn2) }
        }
        val normRes = Series(
            normalizeExperimentalData(
                picket.sortedExperimentalData,
                table.items.map { FixableValue(it.mn2, it.fixed) }
            ).mapIndexed { i, resApp ->
                Data(picket.sortedExperimentalData[i].ab2 as Number, resApp as Number)
            }.toObservableList()
        )

        chart.data += series
        chart.data += normRes

        normRes.name = "Нормализованная кривая"
    }
}