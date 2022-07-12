package ru.nucodelabs.gem.view.charts

import javafx.beans.value.ObservableObjectValue
import javafx.fxml.FXML
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.stage.Stage
import ru.nucodelabs.data.ves.Section
import ru.nucodelabs.data.ves.picketBoundsOrNull
import ru.nucodelabs.gem.extensions.fx.toObservableList
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.gem.view.control.chart.InterpolationMap
import java.net.URL
import java.util.*
import javax.inject.Inject

class PseudoSectionController @Inject constructor(
    private val sectionObservable: ObservableObjectValue<Section>,
    private val colorMapper: ColorMapper
) : AbstractController() {

    @FXML
    private lateinit var xAxis: NumberAxis

    @FXML
    private lateinit var chart: InterpolationMap

    override val stage: Stage?
        get() = chart.scene.window as Stage?

    override fun initialize(location: URL, resources: ResourceBundle) {
        chart.colorMapper = colorMapper
        sectionObservable.addListener { _, oldValue: Section?, newValue: Section? ->
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
        setupXAxisBounds()

        val section = sectionObservable.get()
        val data: MutableList<XYChart.Data<Number, Number>> = mutableListOf()

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

        chart.data.setAll(XYChart.Series(data.toObservableList()))

        // force
        if (section.pickets.size <= 1) {
            val maxAb2 = section.pickets.first().experimentalData.maxOfOrNull { it.ab2 } ?: 0.1
            xAxis.lowerBound = -maxAb2
            xAxis.upperBound = +maxAb2
        }
    }

    private fun setupXAxisBounds() {
        val section = sectionObservable.value
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
}