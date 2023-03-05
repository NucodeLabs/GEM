package ru.nucodelabs.gem.view.control.chart

import javafx.beans.NamedArg
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.chart.ValueAxis
import javafx.scene.layout.StackPane
import javafx.scene.text.Text

class PolygonWithNamesChart (
    @NamedArg("xAxis") xAxis: ValueAxis<Number>,
    @NamedArg("yAxis") yAxis: ValueAxis<Number>) : PolygonChart(xAxis, yAxis) {
    init {
        animated = false
    }

   private fun updateText(): ObservableList<Node>? {
       for ((s,p) in seriesPolygons) {
           val stack = StackPane()
           val po = p.points
           val text = Text(po[0], po[1], s.name)
           stack.children.addAll(p, text)
           plotChildren.addAll(stack)
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




