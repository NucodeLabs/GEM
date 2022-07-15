package ru.nucodelabs.gem.view.charts

import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.scene.chart.XYChart
import javafx.stage.Stage
import javafx.util.StringConverter
import ru.nucodelabs.data.fx.ObservableSection
import ru.nucodelabs.data.ves.ExperimentalData
import ru.nucodelabs.data.ves.Picket
import ru.nucodelabs.data.ves.picketsBounds
import ru.nucodelabs.data.ves.xOfPicket
import ru.nucodelabs.gem.extensions.fx.toObservableList
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.charts.PseudoSectionController.PicketDependencies.Factory.dependenciesOf
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.gem.view.control.chart.InterpolationMap
import ru.nucodelabs.gem.view.control.chart.NucodeNumberAxis
import ru.nucodelabs.gem.view.control.chart.log.LogarithmicAxis
import java.net.URL
import java.util.*
import javax.inject.Inject

class PseudoSectionController @Inject constructor(
    private val observableSection: ObservableSection,
    private val colorMapper: ColorMapper,
    private val formatter: StringConverter<Number>
) : AbstractController() {

    /**
     * Used for comparing pickets only by data on which chart is dependent
     */
    data class PicketDependencies(
        val experimentalData: List<ExperimentalData>,
        val offsetX: Double,
        val z: Double
    ) {
        companion object Factory {
            fun dependenciesOf(picket: Picket) = PicketDependencies(
                experimentalData = picket.effectiveExperimentalData,
                offsetX = picket.offsetX,
                z = picket.z
            )
        }
    }

    @FXML
    private lateinit var yAxis: LogarithmicAxis

    @FXML
    private lateinit var xAxis: NucodeNumberAxis

    @FXML
    private lateinit var chart: InterpolationMap

    override val stage: Stage?
        get() = chart.scene.window as Stage?

    override fun initialize(location: URL, resources: ResourceBundle) {
        xAxis.tickLabelFormatter = formatter
        yAxis.tickLabelFormatter = formatter

        chart.colorMapper = colorMapper
        observableSection.pickets.addListener(ListChangeListener { c ->
            while (c.next()) {
                if (c.wasReplaced()) {
                    if (c.removed.map { dependenciesOf(it) } != c.addedSubList.map { dependenciesOf(it) }) {
                        update()
                    }
                } else {
                    if (c.wasAdded() || c.wasRemoved() || c.wasPermutated()) {
                        update()
                    }
                }
            }
        })
    }

    private fun update() {
        setupXAxisBounds()
        setupYAxisBounds()

        val section = observableSection.asSection()
        val data: MutableList<XYChart.Data<Number, Number>> = mutableListOf()

        for (picket in section.pickets) {
            for (expData in picket.sortedExperimentalData) {
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
    }

    private fun setupYAxisBounds() {
        if (observableSection.pickets.none { it.sortedExperimentalData.isNotEmpty() }) {
            yAxis.lowerBound = 1.0
            yAxis.upperBound = 1000.0
        } else {
            yAxis.upperBound =
                observableSection.pickets.maxOfOrNull { it.sortedExperimentalData.lastOrNull()?.ab2 ?: 1.0 } ?: 1.0
            yAxis.lowerBound =
                observableSection.pickets.minOfOrNull { it.sortedExperimentalData.firstOrNull()?.ab2 ?: 1.0 } ?: 1.0
        }
    }

    private fun setupXAxisBounds() {
        val bounds = observableSection.asSection().picketsBounds()
        xAxis.lowerBound = bounds.firstOrNull()?.leftX ?: 0.0
        xAxis.upperBound = bounds.lastOrNull()?.rightX?.takeIf { it > 0.0 } ?: 100.0
    }
}