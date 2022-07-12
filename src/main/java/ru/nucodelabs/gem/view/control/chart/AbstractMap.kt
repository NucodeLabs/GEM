package ru.nucodelabs.gem.view.control.chart

import javafx.beans.NamedArg
import javafx.scene.canvas.Canvas
import javafx.scene.chart.ScatterChart
import javafx.scene.chart.ValueAxis
import javafx.scene.layout.Region
import ru.nucodelabs.gem.extensions.fx.clear

abstract class AbstractMap(
    @NamedArg("xAxis") xAxis: ValueAxis<Number>,
    @NamedArg("yAxis") yAxis: ValueAxis<Number>
) : ScatterChart<Number, Number>(xAxis, yAxis) {

    private val plotArea = this.lookup(".chart-plot-background") as Region

    protected val canvas: Canvas = Canvas(plotArea.width, plotArea.height)

    init {
        plotChildren += canvas
        canvas.layoutX = 0.0
        canvas.layoutY = 0.0
        canvas.widthProperty().bind(plotArea.widthProperty())
        canvas.heightProperty().bind(plotArea.heightProperty())
        canvas.viewOrder = 1.0
    }

    override fun layoutPlotChildren() {
        super.layoutPlotChildren()
        canvas.clear()
        draw(canvas)
    }

    abstract fun draw(canvas: Canvas)
}