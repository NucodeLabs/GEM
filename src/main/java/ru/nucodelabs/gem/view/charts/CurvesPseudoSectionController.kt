package ru.nucodelabs.gem.view.charts

import javafx.fxml.FXML
import javafx.scene.chart.LineChart
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Data
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.util.StringConverter
import ru.nucodelabs.geo.ves.calc.graph.CurvesChartParser
import ru.nucodelabs.data.fx.ObservableSection
import ru.nucodelabs.geo.ves.ExperimentalData
import ru.nucodelabs.geo.ves.calc.effectiveToSortedIndicesMapping
import ru.nucodelabs.gem.extensions.fx.forCharts
import ru.nucodelabs.gem.extensions.fx.toObservableList
import ru.nucodelabs.gem.view.control.chart.installTooltips
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

class CurvesPseudoSectionController @Inject constructor(
    formatter: StringConverter<Number>,
    observableSection: ObservableSection,
    private val decimalFormat: DecimalFormat
) : AbstractPseudoSectionController(observableSection, formatter) {

    @FXML
    lateinit var title: Label

    @FXML
    private lateinit var chart: LineChart<Number, Number>

    private val pointMap = mutableMapOf<Data<Number, Number>, Pair<Int, ExperimentalData>>()

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        chart.installTooltips(::tooltipFactory)
    }

    override fun update() {
        pointMap.clear()

        chart.data.clear()
        chart.data += CurvesChartParser(observableSection.asSection()).getPoints().mapIndexed { picketIndex, curve ->
            val picket = observableSection.pickets[picketIndex]
            val indexMapping = picket.effectiveToSortedIndicesMapping()
            val sorted = picket.sortedExperimentalData

            XYChart.Series(curve.mapIndexed { dataIndex, p ->
                Data(
                    p.x as Number,
                    p.y as Number
                ).also { pointMap += it to (indexMapping[dataIndex] to sorted[indexMapping[dataIndex]]) }
            }.toObservableList())
        }
    }

    private fun tooltipFactory(
        seriesIndex: Int,
        series: XYChart.Series<Number, Number>,
        pointIndex: Int,
        point: Data<Number, Number>
    ): Tooltip {
        val pair = pointMap[point]
        val n = pair?.first?.plus(1)
        val ab2 = decimalFormat.format(pair?.second?.ab2)
        val res = decimalFormat.format(pair?.second?.resistanceApparent)
        return Tooltip(
            """
                №$n
                AB/2 = $ab2 m
                ρₐ = $res Ω‧m
            """.trimIndent()
        ).forCharts()
    }
}