package ru.nucodelabs.gem.view.control.chart

import javafx.beans.NamedArg
import javafx.scene.Group
import javafx.scene.chart.ValueAxis
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text


class PolygonWithNamesChart(
    @NamedArg("xAxis") xAxis: ValueAxis<Number>,
    @NamedArg("yAxis") yAxis: ValueAxis<Number>
) : PolygonChart(xAxis, yAxis) {
    init {
        animated = false
    }

    val seriesText: Map<Series<Number, Number>, Text>
        get() = _seriesText

    private val _seriesText = mutableMapOf<Series<Number, Number>, Text>()

    private fun updateText() {
        for ((series, polygon) in seriesPolygons) {
            val group = Group()
            group.children.addAll(polygon)
            plotChildren.addAll(group)
            val points = polygon.points
            val text = Text(points[0] + 3, points[1] + 10, series.name).apply {
                font = Font(11.0)
                fill = Color.WHITE
                effect = DropShadow(2.0, Color.BLACK)
                textProperty().bind(series.nameProperty())
            }
            group.children += text
            _seriesText[series] = text
        }
    }


    override fun layoutPlotChildren() {
        super.layoutPlotChildren()
        updateText()
    }

    override fun seriesRemoved(series: Series<Number, Number>) {
        super.seriesRemoved(series)
        updateText()
    }

    override fun seriesAdded(series: Series<Number, Number>, seriesIndex: Int) {
        super.seriesAdded(series, seriesIndex)
        updateText()
    }
}




