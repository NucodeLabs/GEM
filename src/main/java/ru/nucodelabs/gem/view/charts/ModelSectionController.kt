package ru.nucodelabs.gem.view.charts

import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart.Data
import javafx.scene.chart.XYChart.Series
import javafx.stage.Stage
import javafx.util.StringConverter
import ru.nucodelabs.data.fx.ObservableSection
import ru.nucodelabs.data.ves.ModelLayer
import ru.nucodelabs.data.ves.Picket
import ru.nucodelabs.data.ves.picketsBounds
import ru.nucodelabs.data.ves.zOfModelLayers
import ru.nucodelabs.gem.extensions.fx.observableListOf
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.charts.ModelSectionController.PicketDependencies.Factory.dependenciesOf
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.gem.view.control.chart.PolygonChart
import java.math.MathContext
import java.math.RoundingMode
import java.net.URL
import java.util.*
import javax.inject.Inject
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
    data class PicketDependencies(
        val modelData: List<ModelLayer>,
        val offsetX: Double,
        val z: Double
    ) {
        companion object Factory {
            fun dependenciesOf(picket: Picket) = PicketDependencies(
                modelData = picket.modelData,
                offsetX = picket.offsetX,
                z = picket.z
            )
        }
    }

    @FXML
    private lateinit var yAxis: NumberAxis

    @FXML
    private lateinit var xAxis: NumberAxis

    @FXML
    private lateinit var chart: PolygonChart

    override val stage: Stage?
        get() = chart.scene.window as Stage?

    override fun initialize(location: URL?, resources: ResourceBundle?) {
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

        colorMapper.maxValueProperty().addListener { _, _, _ -> update() }
        colorMapper.minValueProperty().addListener { _, _, _ -> update() }
        colorMapper.numberOfSegmentsProperty().addListener { _, _, _ -> update() }
    }

    private fun update() {
        setupXAxisBounds()
        setupYAxisBounds()

        chart.data.clear()

        if (observableSection.pickets.isEmpty()) {
            return
        }

        val lowerBoundZ = zWithVirtualLastLayers().minOfOrNull { it.minOrNull() ?: 0.0 } ?: 0.0

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

                val series: Series<Number, Number> = Series(
                    observableListOf(
                        Data(x, y),
                        Data(x + width, y),
                        Data(x + width, y - height),
                        Data(x, y - height)
                    )
                )

                chart.data += series
                chart.seriesPolygons[series]?.apply { fill = colorMapper.colorFor(picket.modelData[i].resistance) }
            }
        }
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