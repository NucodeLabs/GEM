package ru.nucodelabs.gem.view.control.chart

import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.chart.ValueAxis
import javafx.scene.layout.StackPane
import javafx.scene.text.Text

class PolygonNames (xAxis: ValueAxis<Number>, yAxis: ValueAxis<Number>) : PolygonChart(xAxis, yAxis) {
    init {
        animated = false
    }

   private var pol = seriesPolygons.values
   fun getSeries(): ObservableList<Node>? {
       for (p in pol) {
           val stack = StackPane()
           val po = p.points
           val text = Text(po[0], po[1], "jfrjfrjf")
           stack.children.addAll(p, text)
           plotChildren.addAll(stack)
       }
        return plotChildren
   }

}




