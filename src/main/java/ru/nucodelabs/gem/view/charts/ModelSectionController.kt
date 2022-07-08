package ru.nucodelabs.gem.view.charts

import javafx.beans.value.ObservableObjectValue
import javafx.fxml.FXML
import javafx.scene.chart.AreaChart
import javafx.scene.chart.NumberAxis
import javafx.scene.paint.Color
import javafx.stage.Stage
import ru.nucodelabs.data.ves.Section
import ru.nucodelabs.data.ves.picketBoundsOrNull
import ru.nucodelabs.gem.extensions.fx.Line
import ru.nucodelabs.gem.extensions.fx.Point
import ru.nucodelabs.gem.extensions.fx.observableListOf
import ru.nucodelabs.gem.extensions.fx.toCss
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.color.ColorMapper
import java.net.URL
import java.util.*
import javax.inject.Inject
import kotlin.math.absoluteValue

private const val LAST_COEF = 0.5

class ModelSectionController @Inject constructor(
    private val sectionObservable: ObservableObjectValue<Section>,
    private val colorMapper: ColorMapper
) : AbstractController() {

    @FXML
    private lateinit var yAxis: NumberAxis

    @FXML
    private lateinit var xAxis: NumberAxis

    @FXML
    private lateinit var chart: AreaChart<Double, Double>

    override val stage: Stage?
        get() = chart.scene.window as Stage?

    private val section: Section
        get() = sectionObservable.get()!!

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        sectionObservable.addListener { _, _: Section?, newValue: Section? ->
            if (newValue != null) {
                update()
            }
        }
    }

    // the chart fills area between line and ZERO
    private fun update() {
        chart.data.clear()

        if (section.pickets.isEmpty()) {
            return
        }

        val lowerBoundZ = zWithVirtualLastLayers().minOfOrNull { it.minOrNull() ?: 0.0 } ?: 0.0

        val colors = mutableMapOf<Line<Double, Double>, Color>()
        for (picket in section.pickets) {
            if (picket.modelData.isEmpty()) {
                continue
            }

            val linesForPicket = mutableListOf<Line<Double, Double>>()

            val (leftX, rightX) = section.picketBoundsOrNull(picket) ?: (-0.1 to +0.1)

            // top line
            linesForPicket += Line(
                observableListOf(
                    Point(leftX, picket.z),
                    Point(rightX, picket.z)
                )
            ).also<Line<Double, Double>> {
                colors[it] = if (picket.z > 0) {
                    colorMapper.colorFor(picket.modelData.first().resistance)
                } else {
                    Color.WHITE
                }
            }

            val zOfLayers = picket.zOfModelLayers().dropLast(1)
            for ((i, layerZ) in zOfLayers.withIndex()) {
                linesForPicket += Line(
                    observableListOf(
                        Point(leftX, layerZ),
                        Point(rightX, layerZ)
                    )
                ).also {
                    colors[it] = if (layerZ > 0) {
                        colorMapper.colorFor(picket.modelData[i + 1].resistance)
                    } else {
                        colorMapper.colorFor(picket.modelData[i].resistance)
                    }
                }
            }

            // lower bound line
            linesForPicket += Line(
                observableListOf(
                    Point(leftX, lowerBoundZ),
                    Point(rightX, lowerBoundZ)
                )
            ).also {
                colors[it] = colorMapper.colorFor(picket.modelData.last().resistance)
            }

            chart.data += linesForPicket
        }

        setupStyle(colors)
        setupXAxisBounds()
        setupYAxisBounds()
    }

    private fun setupStyle(colors: MutableMap<Line<Double, Double>, Color>) {
        chart.data.forEach { it.node.lookup(".chart-series-area-fill").style = "-fx-fill: ${colors[it]?.toCss()};" }
        chart.data.sortedBy {
            it.data.first().yValue.absoluteValue
        }.forEachIndexed { index, series ->
            series.node.viewOrder = index.toDouble()
        }
    }

    private fun setupXAxisBounds() {
        when (section.pickets.size) {
            1 -> {
                val (leftX, rightX) = section.picketBoundsOrNull(section.pickets.first()) ?: (-0.1 to +0.1)
                xAxis.lowerBound = leftX
                xAxis.upperBound = rightX
            }
            0 -> {
                xAxis.lowerBound = -3.0
                xAxis.upperBound = 4.0
            }
            else -> {
                val visibleOnPseudo = section.pickets.filter { it.experimentalData.isNotEmpty() }
                xAxis.lowerBound = section.xOfPicket(visibleOnPseudo.first())
                xAxis.upperBound = section.xOfPicket(visibleOnPseudo.last())
            }
        }
    }

    private fun setupYAxisBounds() {
        yAxis.upperBound = section.pickets.maxOf { it.z }
        yAxis.lowerBound = zWithVirtualLastLayers().minOfOrNull { it.minOrNull() ?: 0.0 } ?: 0.0
    }

    private fun zWithVirtualLastLayers(): List<List<Double>> = section.pickets.map {
        it.zOfModelLayers().toMutableList().also { zList ->
            if (zList.size >= 2) {
                zList[zList.lastIndex] = zList[zList.lastIndex - 1]
                zList[zList.lastIndex] -= it.modelData[it.modelData.lastIndex - 1].power * LAST_COEF
            }
        }
    }.filter { it.isNotEmpty() }
}