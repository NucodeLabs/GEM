package ru.nucodelabs.gem.view.charts

import javafx.fxml.FXML
import javafx.scene.chart.LineChart
import javafx.scene.chart.XYChart
import javafx.util.StringConverter
import ru.nucodelabs.algorithms.charts.curves_chart.CurvesChartParser
import ru.nucodelabs.data.fx.ObservableSection
import ru.nucodelabs.gem.extensions.fx.toObservableList
import java.net.URL
import java.util.*
import javax.inject.Inject

class CurvesPseudoSectionController @Inject constructor(
    formatter: StringConverter<Number>,
    observableSection: ObservableSection,
) : AbstractPseudoSectionController(observableSection, formatter) {
    @FXML
    private lateinit var chart: LineChart<Number, Number>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        chart.axisSortingPolicy = LineChart.SortingPolicy.NONE
    }

    override fun update() {
        chart.data.clear()
        chart.data += CurvesChartParser(observableSection.asSection()).getPoints().map { curve ->
            XYChart.Series(curve.map { p ->
                XYChart.Data(
                    p.x as Number,
                    p.y as Number
                )
            }.toObservableList())
        }
    }
}