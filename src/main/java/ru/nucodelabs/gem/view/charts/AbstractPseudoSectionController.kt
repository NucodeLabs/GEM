package ru.nucodelabs.gem.view.charts

import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.stage.Stage
import javafx.util.StringConverter
import ru.nucodelabs.data.fx.ObservableSection
import ru.nucodelabs.data.ves.ExperimentalData
import ru.nucodelabs.data.ves.Picket
import ru.nucodelabs.data.ves.picketsBounds
import ru.nucodelabs.data.ves.xOfPicket
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.charts.AbstractPseudoSectionController.PicketDependencies.Factory.dependenciesOf
import ru.nucodelabs.gem.view.control.chart.NucodeNumberAxis
import ru.nucodelabs.gem.view.control.chart.log.LogarithmicAxis
import java.net.URL
import java.util.*

abstract class AbstractPseudoSectionController constructor(
    protected val observableSection: ObservableSection,
    protected val formatter: StringConverter<Number>
) : AbstractController() {
    /**
     * Used for comparing pickets only by data on which chart is dependent
     */
    protected data class PicketDependencies(
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
    protected lateinit var yAxis: LogarithmicAxis

    @FXML
    protected lateinit var xAxis: NucodeNumberAxis

    override val stage: Stage?
        get() = xAxis.scene.window as Stage?

    override fun initialize(location: URL, resources: ResourceBundle) {
        xAxis.tickLabelFormatter = formatter
        yAxis.tickLabelFormatter = formatter

        observableSection.pickets.addListener(ListChangeListener { c ->
            while (c.next()) {
                if (c.wasReplaced()) {
                    if (c.removed.map { dependenciesOf(it) } != c.addedSubList.map { dependenciesOf(it) }) {
                        setupXAxisBounds()
                        setupXAxisMarks()
                        setupYAxisBounds()
                        update()
                    }
                } else {
                    if (c.wasAdded() || c.wasRemoved() || c.wasPermutated()) {
                        setupXAxisBounds()
                        setupXAxisMarks()
                        setupYAxisBounds()
                        update()
                    }
                }
            }
        })
    }

    abstract fun update()

    protected fun setupXAxisMarks() {
        val section = observableSection.asSection()
        xAxis.forceMarks.setAll(
            section.picketsBounds().flatMap { listOf(it.leftX, it.rightX) }.distinct()
                    + section.pickets.indices.map { section.xOfPicket(it) }.distinct()
        )
    }

    protected fun setupYAxisBounds() {
        if (observableSection.pickets.none { it.effectiveExperimentalData.isNotEmpty() }
            || observableSection.pickets.isEmpty()) {
            yAxis.lowerBound = 1.0
            yAxis.upperBound = 1000.0
        } else {
            yAxis.upperBound =
                observableSection.pickets.filter { it.effectiveExperimentalData.isNotEmpty() }.maxOf {
                    it.effectiveExperimentalData.last().ab2
                }
            yAxis.lowerBound =
                observableSection.pickets.filter { it.effectiveExperimentalData.isNotEmpty() }.minOf {
                    it.effectiveExperimentalData.first().ab2
                }
        }
    }

    protected fun setupXAxisBounds() {
        val bounds = observableSection.asSection().picketsBounds()
        xAxis.lowerBound = bounds.firstOrNull()?.leftX ?: 0.0
        xAxis.upperBound = bounds.lastOrNull()?.rightX?.takeIf { it > 0.0 } ?: 100.0
    }
}