package ru.nucodelabs.gem.view.charts

import javafx.beans.value.ObservableObjectValue
import javafx.fxml.FXML
import javafx.scene.chart.AreaChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart.Data
import javafx.scene.chart.XYChart.Series
import javafx.stage.Stage
import ru.nucodelabs.data.ves.Section
import ru.nucodelabs.gem.extensions.fx.observableListOf
import ru.nucodelabs.gem.view.AbstractController
import java.net.URL
import java.util.*
import javax.inject.Inject

class CrossSectionController @Inject constructor(
    private val sectionObservable: ObservableObjectValue<Section>,
) : AbstractController() {

    companion object {
        const val LAST_COEF = 0.5
    }

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
                && newValue.pickets.map { it.modelData } != oldValue?.pickets?.map { it.modelData }
            ) {
                update()
            }
        }
    }

    private fun update() {
        fun setupAxisBounds() {
            xAxis.lowerBound = section.xOfPicket(section.pickets.first())
            xAxis.upperBound = section.xOfPicket(section.pickets.last())
        }

        chart.data.clear()

        var lowerBoundZ = section.pickets.minOf { it.zOfModelLayers().dropLast(1).last() }
        lowerBoundZ += lowerBoundZ * LAST_COEF

        for ((index, picket) in section.pickets.withIndex()) {
            val linesForPicket = mutableListOf<Series<Double, Double>>()

            val leftX: Double = if (index == 0) {
                section.xOfPicket(picket)
            } else {
                section.xOfPicket(section.pickets[index - 1]) + (picket.offsetX / 2)
            }

            val rightX: Double = if (index == section.pickets.lastIndex) {
                section.xOfPicket(picket)
            } else {
                section.xOfPicket(picket) + (section.pickets[index + 1].offsetX / 2)
            }

            for (layerZ in picket.zOfModelLayers().dropLast(1)) {
                linesForPicket += Series(
                    observableListOf(
                        Data(leftX, layerZ),
                        Data(rightX, layerZ)
                    )
                )
            }

            linesForPicket += Series(
                observableListOf(
                    Data(leftX, lowerBoundZ),
                    Data(rightX, lowerBoundZ)
                )
            )

            chart.data += linesForPicket
        }

        setupAxisBounds()
    }
}