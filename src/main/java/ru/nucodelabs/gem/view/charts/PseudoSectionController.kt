package ru.nucodelabs.gem.view.charts

import javafx.beans.value.ObservableObjectValue
import javafx.fxml.FXML
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.stage.Stage
import ru.nucodelabs.data.ves.Section
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.gem.view.usercontrols.heatmap.HeatMap
import java.net.URL
import java.util.*
import javax.inject.Inject

class PseudoSectionController @Inject constructor(
    private val sectionObservableObjectValue: ObservableObjectValue<Section>,
    private val colorMapper: ColorMapper
) : AbstractController() {

    @FXML
    private lateinit var xAxis: NumberAxis

    @FXML
    private lateinit var chart: HeatMap

    override val stage: Stage?
        get() = chart.scene.window as Stage?

    override fun initialize(location: URL, resources: ResourceBundle) {
        chart.colorPalette = colorMapper
        sectionObservableObjectValue.addListener { _, oldValue: Section?, newValue: Section? ->
            if (newValue != null
                && (newValue.pickets.map { it.experimentalData } != oldValue?.pickets?.map { it.experimentalData }
                        || newValue.pickets.map { it.offsetX } != oldValue.pickets.map { it.offsetX }
                        || newValue.pickets.map { it.z } != oldValue.pickets.map { it.z })
            ) {
                update()
            }
        }
    }

    private fun update() {
        val section = sectionObservableObjectValue.get()
        val data: MutableList<XYChart.Data<Double, Double>> = mutableListOf()

        for (picket in section.pickets) {
            for (expData in picket.experimentalData) {
                data.add(
                    XYChart.Data(
                        section.xOfPicket(picket),
                        expData.ab2,
                        expData.resistanceApparent
                    )
                )
            }
        }

        chart.data.setAll(data)

        // force
        if (section.pickets.size <= 1) {
            val maxAb2 = section.pickets.first().experimentalData.maxOfOrNull { it.ab2 } ?: 0.1
            xAxis.lowerBound = -maxAb2
            xAxis.upperBound = +maxAb2
        }

    }
}