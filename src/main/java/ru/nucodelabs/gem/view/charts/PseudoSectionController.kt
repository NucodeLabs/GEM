package ru.nucodelabs.gem.view.charts

import javafx.beans.value.ObservableObjectValue
import javafx.fxml.FXML
import javafx.scene.chart.XYChart.Data
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
        val data: MutableList<Data<Double, Double>> = mutableListOf()

        if (section.pickets.size == 1) {
            val picket = section.pickets.first()
            val maxAb2 = picket.experimentalData.maxOfOrNull { it.ab2 } ?: 0.1
            for (expData in picket.experimentalData) {
                data += Data(
                    -maxAb2,
                    expData.ab2,
                    expData.resistanceApparent
                )
                data += Data(
                    +maxAb2,
                    expData.ab2,
                    expData.resistanceApparent
                )
            }
        } else {
            for (picket in section.pickets) {
                for (expData in picket.experimentalData) {
                    data.add(
                        Data(
                            section.xOfPicket(picket),
                            expData.ab2,
                            expData.resistanceApparent
                        )
                    )
                }
            }
        }
        chart.data.setAll(data)
    }
}