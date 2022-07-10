package ru.nucodelabs.gem.view.charts

import javafx.fxml.FXML
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart.Data
import javafx.scene.chart.XYChart.Series
import javafx.stage.Stage
import ru.nucodelabs.gem.extensions.fx.observableListOf
import ru.nucodelabs.gem.extensions.fx.toObservableList
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.color.ColorMapper
import java.net.URL
import java.util.*
import javax.inject.Inject

class ColorAxisController @Inject constructor(
    private val colorMapper: ColorMapper
) : AbstractController() {
    @FXML
    private lateinit var yAxis: NumberAxis

    @FXML
    private lateinit var chart: PolygonChart
    override val stage: Stage?
        get() = chart.scene.window as Stage?

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        colorMapper.minValueProperty().addListener { _, _, _ -> update() }
        colorMapper.maxValueProperty().addListener { _, _, _ -> update() }
        colorMapper.numberOfSegmentsProperty().addListener { _, _, _ -> update() }
        update()
    }

    private fun update() {
        val range = colorMapper.maxValue - colorMapper.minValue
        chart.data = colorMapper.segments.map {
            Series(
                observableListOf(
                    Data(0.0, it.from * range),
                    Data(100.0, it.from * range),
                    Data(100.0, it.to * range),
                    Data(0.0, it.to * range)
                )
            ) as Series<Number, Number>
        }.toObservableList()

        chart.data.forEachIndexed { index, series ->
            chart.seriesPolygons[series]?.apply { fill = colorMapper.segments[index].color }
        }

        yAxis.lowerBound = colorMapper.minValue
        yAxis.upperBound = colorMapper.maxValue
    }
}