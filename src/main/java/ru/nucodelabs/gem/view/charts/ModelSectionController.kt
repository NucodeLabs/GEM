package ru.nucodelabs.gem.view.charts

import javafx.beans.value.ObservableObjectValue
import javafx.fxml.FXML
import javafx.scene.chart.AreaChart
import javafx.scene.chart.NumberAxis
import javafx.scene.paint.Color
import javafx.stage.Stage
import ru.nucodelabs.data.ves.Section
import ru.nucodelabs.data.ves.picketBounds
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

class ModelSectionController @Inject constructor(
    private val sectionObservable: ObservableObjectValue<Section>,
    private val colorMapper: ColorMapper
) : AbstractController() {

    private val LAST_COEF = 0.5
    private val SINGLE_PICKET_LEFT_X = 0.0
    private val SINGLE_PICKET_RIGHT_X = 100.0

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
        sectionObservable.addListener { _, oldValue: Section?, newValue: Section? ->
            if (newValue != null
                && (newValue.pickets.map { it.modelData } != oldValue?.pickets?.map { it.modelData }
                        || newValue.pickets.map { it.z } != oldValue.pickets.map { it.z })
            ) {
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

        val lowerBoundZ = zWithVirtualLastLayers().minOf { it.min() }

        val colors = mutableMapOf<Line<Double, Double>, Color>()
        for (picket in section.pickets) {
            if (picket.modelData.isEmpty()) {
                continue
            }

            val linesForPicket = mutableListOf<Line<Double, Double>>()

            val (leftX, rightX) = if (section.pickets.size > 1) {
                section.picketBounds(picket)
            } else {
                SINGLE_PICKET_LEFT_X to SINGLE_PICKET_RIGHT_X
            }

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
        if (section.pickets.size > 1) {
            xAxis.lowerBound = section.xOfPicket(section.pickets.first())
            xAxis.upperBound = section.xOfPicket(section.pickets.last())
        } else {
            xAxis.lowerBound = SINGLE_PICKET_LEFT_X
            xAxis.upperBound = SINGLE_PICKET_RIGHT_X
        }
    }

    private fun setupYAxisBounds() {
        yAxis.upperBound = section.pickets.maxOf { it.z }
        yAxis.lowerBound = zWithVirtualLastLayers().minOf { it.min() }
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