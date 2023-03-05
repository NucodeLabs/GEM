package ru.nucodelabs.gem.view.control.chart

import javafx.beans.NamedArg
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.chart.ValueAxis
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import smile.vq.Neighborhood.bubble


class PolygonWithNamesChart (
    @NamedArg("xAxis") xAxis: ValueAxis<Number>,
    @NamedArg("yAxis") yAxis: ValueAxis<Number>) : PolygonChart(xAxis, yAxis) {
    init {
        animated = false
    }

   private fun updateText(): ObservableList<Node>? {
       for ((s,p) in seriesPolygons) {
           val group = Group()
           group.children.addAll(p)
           plotChildren.addAll(group)
           val po = p.points
           val text = Text(po[0], po[1]+15, s.name)
           text.font = Font(17.0)
           text.fill = Color.WHITE
           text.strokeWidth = 0.6
           text.stroke = Color.BLACK
           group.children.addAll(text)
       }
       return plotChildren
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




