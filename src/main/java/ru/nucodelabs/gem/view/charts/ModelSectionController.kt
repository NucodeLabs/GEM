package ru.nucodelabs.gem.view.charts

import javafx.beans.value.ObservableObjectValue
import javafx.fxml.FXML
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart.Data
import javafx.scene.chart.XYChart.Series
import javafx.stage.Stage
import ru.nucodelabs.data.ves.Section
import ru.nucodelabs.data.ves.picketBoundsOrNull
import ru.nucodelabs.gem.extensions.fx.observableListOf
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.color.ColorMapper
import java.net.URL
import java.util.*
import javax.inject.Inject
import kotlin.math.abs

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
    private lateinit var chart: PolygonChart

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

        colorMapper.maxValueProperty().addListener { _, _, _ -> update() }
        colorMapper.minValueProperty().addListener { _, _, _ -> update() }
        colorMapper.numberOfSegmentsProperty().addListener { _, _, _ -> update() }
    }

    private fun update() {
        chart.data.clear()

        if (section.pickets.isEmpty()) {
            return
        }

        val lowerBoundZ = zWithVirtualLastLayers().minOfOrNull { it.minOrNull() ?: 0.0 } ?: 0.0

        for (picket in section.pickets) {
            if (picket.modelData.isEmpty()) {
                continue
            }

            val (leftX, rightX) = section.picketBoundsOrNull(picket) ?: (-0.1 to +0.1)

            val zList = picket.zOfModelLayers()
            for (i in zList.indices) {
                val x = leftX
                val y = if (i == 0) picket.z else zList[i - 1]
                val width = abs(rightX - leftX)
                val height = if (i == zList.lastIndex) abs(zList[i - 1] - lowerBoundZ) else picket.modelData[i].power

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

        setupXAxisBounds()
        setupYAxisBounds()
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