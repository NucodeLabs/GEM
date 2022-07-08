package ru.nucodelabs.gem.view.charts

import javafx.beans.NamedArg
import javafx.beans.binding.Bindings.createDoubleBinding
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.ScatterChart
import javafx.scene.shape.Rectangle
import ru.nucodelabs.gem.extensions.fx.observableListOf

class RectangleChart(
    @NamedArg("xAxis") private val xAxis: NumberAxis,
    @NamedArg("yAxis") private val yAxis: NumberAxis
) : ScatterChart<Number, Number>(xAxis, yAxis) {

    private val _rectangles: MutableList<Rectangle> = mutableListOf()

    val rectangles: List<Rectangle>
        get() = _rectangles

    /**
     * Adds rectangle and its points as series of data. X, Y, width, height properties are bound.
     */
    fun addRectangle(x: Double, y: Double, width: Double, height: Double, config: Rectangle.() -> Unit = {}) =
        Rectangle().apply {
            this.x = xAxis.getDisplayPosition(x)
            this.xProperty().bind(
                createDoubleBinding(
                    { xAxis.getDisplayPosition(x) },
                    this@RectangleChart.layoutBoundsProperty(),
                    this@RectangleChart.boundsInParentProperty(),
                    this@RectangleChart.boundsInLocalProperty()
                )
            )

            this.y = yAxis.getDisplayPosition(y + height)
            this.yProperty().bind(
                createDoubleBinding(
                    { yAxis.getDisplayPosition(y + height) },
                    this@RectangleChart.layoutBoundsProperty(),
                    this@RectangleChart.boundsInParentProperty(),
                    this@RectangleChart.boundsInLocalProperty()
                )
            )

            this.width = xAxis.getDisplayPosition(width)
            this.widthProperty().bind(
                createDoubleBinding(
                    { xAxis.getDisplayPosition(width) },
                    this@RectangleChart.layoutBoundsProperty(),
                    this@RectangleChart.boundsInParentProperty(),
                    this@RectangleChart.boundsInLocalProperty()
                )
            )

            this.height = yAxis.getDisplayPosition(yAxis.upperBound - height)
            this.heightProperty().bind(
                createDoubleBinding(
                    { yAxis.getDisplayPosition(yAxis.upperBound - height) },
                    this@RectangleChart.layoutBoundsProperty(),
                    this@RectangleChart.boundsInParentProperty(),
                    this@RectangleChart.boundsInLocalProperty()
                )
            )

            _rectangles += this

            data += Series(
                observableListOf(
                    Data(x, y),
                    Data(x + width, y),
                    Data(x, y + height),
                    Data(x + width, y + height)
                )
            )

            plotChildren += this
        }.also { it.config() }
}