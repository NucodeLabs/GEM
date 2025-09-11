package ru.nucodelabs.gem.view.controller.charts

import jakarta.inject.Inject
import javafx.fxml.FXML
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Data
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.effect.BlendMode
import javafx.stage.Stage
import javafx.util.StringConverter
import ru.nucodelabs.gem.fxmodel.ves.ObservableSection
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.gem.view.control.chart.InterpolationMap
import ru.nucodelabs.geo.ves.calc.effectiveToSortedIndicesMapping
import ru.nucodelabs.geo.ves.calc.xOfPicket
import ru.nucodelabs.kfx.ext.installTooltips
import ru.nucodelabs.kfx.ext.shownOnHover
import ru.nucodelabs.kfx.ext.toObservableList
import java.net.URL
import java.text.DecimalFormat
import java.util.*

class MapPseudoSectionController @Inject constructor(
    private val colorMapper: ColorMapper,
    formatter: StringConverter<Number>,
    observableSection: ObservableSection,
    private val decimalFormat: DecimalFormat
) : AbstractPseudoSectionController(observableSection, formatter) {

    @FXML
    lateinit var title: Label

    @FXML
    private lateinit var chart: InterpolationMap

    private val pointMap = mutableMapOf<Data<Number, Number>, Int>()

    override val stage: Stage?
        get() = chart.scene.window as Stage?

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        chart.colorMapper = colorMapper
        chart.installTooltips(::tooltipFactory)
        chart.canvasBlendMode = BlendMode.MULTIPLY
    }

    override fun update() {
        pointMap.clear()

        val section = observableSection.asSection()
        val data: MutableList<Data<Number, Number>> = mutableListOf()

        for (picket in section.pickets) {
            val indexMapping = picket.effectiveToSortedIndicesMapping()
            for ((index, expData) in picket.effectiveExperimentalData.withIndex()) {
                data.add(
                    Data(
                        section.xOfPicket(picket) as Number,
                        expData.ab2 as Number,
                        expData.resistanceApparent
                    ).also { pointMap += it to indexMapping[index] }
                )
            }
        }

        chart.data.setAll(XYChart.Series(data.toObservableList()))
    }

    @Suppress("UNUSED_PARAMETER")
    private fun tooltipFactory(
        seriesIndex: Int,
        series: XYChart.Series<Number, Number>,
        pointIndex: Int,
        point: Data<Number, Number>
    ): Tooltip {
        return Tooltip(
            """
                №${pointMap[point]?.plus(1)}
                AB/2 = ${decimalFormat.format(point.yValue)} m
                ρₐ = ${decimalFormat.format(point.extraValue)} Ω‧m
            """.trimIndent()
        ).shownOnHover()
    }
}
