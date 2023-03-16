package ru.nucodelabs.gem.view.control.chart

import javafx.beans.NamedArg
import javafx.scene.Group
import javafx.scene.chart.ValueAxis
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.shape.Polygon
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

    val textGroup: Map<Series<Number,Number>,Group>
        get() = _group

    private val _seriesText = mutableMapOf<Series<Number, Number>, Text>()
    private val _group = mutableMapOf<Series<Number,Number>,Group>()

    private var flag = false

    private fun removeGroup(series: Series<Number, Number>){
        plotChildren -= _group[series]
        _group.keys -= series
        textRemoved(series)
    }
     private fun textRemoved(series: Series<Number, Number>){
         plotChildren -= _seriesText[series]
         _seriesText.keys -= series
    }

    override fun layoutPlotChildren() {
        super.layoutPlotChildren()
        updateText()
    }

    override fun seriesRemoved(series: Series<Number, Number>) {
        super.seriesRemoved(series)
        removeGroup(series)
    }

    override fun seriesAdded(series: Series<Number, Number>, seriesIndex: Int) {
        super.seriesAdded(series, seriesIndex)
        updateText()
    }

    private fun updateText() {
        var i = 0
        if (flag){
            return
        }
        for ((series, polygon) in seriesPolygons) {
            val points = polygon.points
            if (series.name == null){
                continue
            }
            val text = Text(points[0] + 3, points[1] + 10, series.name).apply {
                font = Font(11.0)
                fill = Color.WHITE
                effect = DropShadow(2.0, Color.BLACK)
                textProperty().bind(series.nameProperty())
            }
            i++
            val group = Group()
            group.children.addAll(polygon)
            plotChildren.addAll(group)
            _group[series] = group
            _group.getOrPut(series){
                group
            }.children += text
            _seriesText.getOrPut(series){
                text
            }
            if (i == seriesPolygons.size){
                flag = true
            }
        }
    }
}




