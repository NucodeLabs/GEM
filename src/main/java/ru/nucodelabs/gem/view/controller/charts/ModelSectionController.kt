package ru.nucodelabs.gem.view.controller.charts

import jakarta.inject.Inject
import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.scene.chart.XYChart.Data
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.Label
import javafx.stage.Stage
import javafx.util.StringConverter
import ru.nucodelabs.gem.fxmodel.ves.ObservableSection
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.gem.view.control.chart.CanvasRenderPolygonChart
import ru.nucodelabs.gem.view.control.chart.InvertibleValueAxis
import ru.nucodelabs.gem.view.control.chart.NucodeNumberAxis
import ru.nucodelabs.gem.view.controller.AbstractController
import ru.nucodelabs.gem.view.controller.charts.ModelSectionController.PicketDependencies.Factory.dependenciesOf
import ru.nucodelabs.geo.ves.ExperimentalData
import ru.nucodelabs.geo.ves.ModelLayer
import ru.nucodelabs.geo.ves.Picket
import ru.nucodelabs.geo.ves.calc.picketsBounds
import ru.nucodelabs.geo.ves.calc.xOfPicket
import ru.nucodelabs.geo.ves.calc.zOfModelLayers
import ru.nucodelabs.kfx.ext.observableListOf
import java.math.MathContext
import java.math.RoundingMode
import java.net.URL
import java.util.*
import kotlin.math.abs

private const val LAST_COEF = 0.5

class ModelSectionController @Inject constructor(
    private val observableSection: ObservableSection,
    private val colorMapper: ColorMapper,
    private val formatter: StringConverter<Number>
) : AbstractController() {

    /**
     * Used for comparing pickets only by data on which chart is dependent
     */
    private data class PicketDependencies(
        val modelData: List<ModelLayer>,
        val offsetX: Double,
        val z: Double,
        val experimentalData: List<ExperimentalData>
    ) {
        companion object Factory {
            fun dependenciesOf(picket: Picket) = PicketDependencies(
                modelData = picket.modelData,
                offsetX = picket.offsetX,
                z = picket.z,
                experimentalData = picket.effectiveExperimentalData
            )
        }
    }

    @FXML
    lateinit var title: Label

    @FXML
    private lateinit var yAxis: InvertibleValueAxis<Number>

    @FXML
    private lateinit var xAxis: NucodeNumberAxis

    @FXML
    lateinit var chart: CanvasRenderPolygonChart

    override val stage: Stage?
        get() = chart.scene.window as Stage?

    override fun initialize(location: URL, resources: ResourceBundle) {
        chart.colorMapper = this.colorMapper
        chart.extraValueFormatter = formatter
        yAxis.tickLabelFormatter = formatter
        xAxis.tickLabelFormatter = formatter

        observableSection.pickets.addListener(ListChangeListener { c ->
            while (c.next()) {
                if (c.wasReplaced()) {
                    if (c.removed.map { dependenciesOf(it) } != c.addedSubList.map { dependenciesOf(it) }) {
                        update()
                    }
                } else {
                    if (c.wasRemoved() || c.wasAdded() || c.wasPermutated()) {
                        update()
                    }
                }
            }
        })
    }

    private fun update() {
        setupXAxisBounds()
        setupXAxisMarks()
        setupYAxisBounds()

        if (observableSection.pickets.isEmpty()) {
            chart.data.clear()
            return
        }

        val lowerBoundZ = zWithVirtualLastLayers().minOfOrNull { it.minOrNull() ?: 0.0 } ?: 0.0

        val chartData = ArrayList<Series<Number, Number>>()
        for ((index, bounds) in observableSection.asSection().picketsBounds().withIndex()) {
            val picket = observableSection.pickets[index]
            if (picket.modelData.isEmpty()) {
                continue
            }

            val (leftX, rightX) = bounds
            val zList = picket.zOfModelLayers()
            for (i in zList.indices) {
                val x = leftX
                val y = if (i == 0) picket.z else zList[i - 1]
                val width = rightX - leftX
                val height = if (i == zList.lastIndex) {
                    if (zList.size == 1) {
                        picket.z - lowerBoundZ
                    } else {
                        abs(zList[i - 1] - lowerBoundZ)
                    }
                } else {
                    picket.modelData[i].power
                }

                val resistance = picket.modelData[i].resistance
                val series: Series<Number, Number> = Series(
                    observableListOf(
                        Data(x, y, resistance),
                        Data(x + width, y, resistance),
                        Data(x + width, y - height, resistance),
                        Data(x, y - height, resistance)
                    )
                )

                chartData += series
            }
        }

        chart.data.setAll(chartData)
    }

    private fun setupXAxisMarks() {
        val section = observableSection.asSection()
        xAxis.forceMarks.setAll(
            section.picketsBounds().flatMap { listOf(it.leftX, it.rightX) }.distinct()
                    + section.pickets.indices.map { section.xOfPicket(it) }.distinct()
        )
    }

    private fun setupXAxisBounds() {
        val bounds = observableSection.asSection().picketsBounds()
        xAxis.lowerBound = bounds.firstOrNull()?.leftX ?: 0.0
        xAxis.upperBound = bounds.lastOrNull()?.rightX?.takeIf { it > 0.0 } ?: 100.0
    }

    private fun setupYAxisBounds() {
        if (observableSection.pickets.any { it.modelData.isNotEmpty() }) {
            yAxis.upperBound = observableSection.pickets.maxOfOrNull { it.z } ?: 100.0
        } else {
            yAxis.upperBound = 100.0
        }
        yAxis.lowerBound = zWithVirtualLastLayers().minOfOrNull { it.minOrNull() ?: 0.0 } ?: 0.0
    }

    private fun zWithVirtualLastLayers(): List<List<Double>> = observableSection.pickets.map {
        it.zOfModelLayers().toMutableList().also { zList ->
            if (zList.size >= 2) {
                zList[zList.lastIndex] = zList[zList.lastIndex - 1]
                zList[zList.lastIndex] -= it.modelData[it.modelData.lastIndex - 1].power * LAST_COEF
                zList[zList.lastIndex] =
                    zList[zList.lastIndex]
                        .toBigDecimal()
                        .round(MathContext(3, RoundingMode.UP))
                        .toDouble()
            }
        }
    }.filter { it.isNotEmpty() }
}