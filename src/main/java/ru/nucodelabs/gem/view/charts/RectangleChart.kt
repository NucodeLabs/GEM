package ru.nucodelabs.gem.view.charts

import javafx.beans.NamedArg
import javafx.beans.binding.Bindings.createDoubleBinding
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.ScatterChart
import ru.nucodelabs.gem.extensions.fx.observableListOf
import javafx.scene.shape.Rectangle as FXRectangle

class RectangleChart(
    @NamedArg("xAxis") private val xAxis: NumberAxis,
    @NamedArg("yAxis") private val yAxis: NumberAxis
) : ScatterChart<Number, Number>(xAxis, yAxis) {

    init {
        animated = false
    }

    /**
     * Values for axes
     */
    class Rectangle(x: Double, y: Double, width: Double, height: Double, init: Rectangle.() -> Unit = {}) :
        FXRectangle() {
        init {
            this.init()
        }

        val xOnAxisProperty: DoubleProperty = SimpleDoubleProperty(x)
        val yOnAxisProperty: DoubleProperty = SimpleDoubleProperty(y)
        val widthOnAxisProperty: DoubleProperty = SimpleDoubleProperty(width)
        val heightOnAxisProperty: DoubleProperty = SimpleDoubleProperty(height)

        var xOnAxis: Double
            get() = xOnAxisProperty.value
            set(value) = xOnAxisProperty.set(value)

        var yOnAxis: Double
            get() = yOnAxisProperty.value
            set(value) = yOnAxisProperty.set(value)

        var widthOnAxis: Double
            get() = widthOnAxisProperty.value
            set(value) = widthOnAxisProperty.set(value)

        var heightOnAxis: Double
            get() = heightOnAxisProperty.value
            set(value) = heightOnAxisProperty.set(value)

        override fun toString(): String =
            "${super.toString()},\n\taxisValues[x = $xOnAxis, y = $yOnAxis, width = $widthOnAxis, height = $heightOnAxis]"
    }

    private val _rectangleSeries: MutableList<Pair<Rectangle, Series<Number, Number>>> = mutableListOf()
    val rectangles: ObservableList<Rectangle> = observableListOf()

    init {
        rectangles.addListener(ListChangeListener { change ->
            while (change.next()) {
                change.addedSubList.forEach { setupRectangle(it) }
                change.removed.forEach {
                    removeSeriesFor(it)
                    removeRectangle(it)
                }
            }
        })

        val dataListener = ListChangeListener<Series<Number, Number>> { change ->
            while (change.next()) {
                change.removed.forEach { series ->
                    rectangleSeries.filter { it.second == series }.forEach { removeRectangle(it.first) }
                }
            }
        }

        data.addListener(dataListener)
        dataProperty().addListener { _, _, newValue ->
            newValue.addListener(dataListener)
        }
    }

    /**
     * Rectangles and associated series
     */
    val rectangleSeries: List<Pair<Rectangle, Series<Number, Number>>>
        get() = _rectangleSeries

    private val sizeProperties
        get() = arrayOf(
            layoutBoundsProperty(),
            boundsInParentProperty(),
            boundsInLocalProperty()
        )

    private fun setupRectangle(rectangle: Rectangle) {
        plotChildren += rectangle

        rectangle.x = xAxis.getDisplayPosition(rectangle.xOnAxis)
        rectangle.xProperty().bind(
            createDoubleBinding(
                { xAxis.getDisplayPosition(rectangle.xOnAxis) },
                rectangle.xOnAxisProperty,
                *sizeProperties
            )
        )

        rectangle.y = yAxis.getDisplayPosition(rectangle.yOnAxis)
        rectangle.yProperty().bind(
            createDoubleBinding(
                { yAxis.getDisplayPosition(rectangle.yOnAxis) },
                rectangle.yOnAxisProperty,
                *sizeProperties
            )
        )

        rectangle.width = xAxis.getDisplayPosition(rectangle.widthOnAxis + xAxis.lowerBound)
        rectangle.widthProperty().bind(
            createDoubleBinding(
                { xAxis.getDisplayPosition(rectangle.widthOnAxis + xAxis.lowerBound) },
                rectangle.widthOnAxisProperty,
                xAxis.lowerBoundProperty(),
                *sizeProperties
            )
        )

        rectangle.height = yAxis.getDisplayPosition(yAxis.upperBound - rectangle.heightOnAxis)
        rectangle.heightProperty().bind(
            createDoubleBinding(
                { yAxis.getDisplayPosition(yAxis.upperBound - rectangle.heightOnAxis) },
                rectangle.heightOnAxisProperty,
                yAxis.upperBoundProperty(),
                *sizeProperties
            )
        )

        val series = Series(
            observableListOf(
                Data(
                    rectangle.xOnAxis as Number,
                    rectangle.yOnAxis as Number
                ).also {
                    it.XValueProperty().bind(rectangle.xOnAxisProperty.asObject())
                    it.YValueProperty().bind(rectangle.yOnAxisProperty.asObject())
                },
                Data(
                    (rectangle.xOnAxis + rectangle.widthOnAxis) as Number,
                    rectangle.yOnAxis as Number
                ).also {
                    it.XValueProperty().bind(rectangle.xOnAxisProperty.add(rectangle.widthOnAxisProperty).asObject())
                    it.YValueProperty().bind(rectangle.yOnAxisProperty.asObject())
                },
                Data(
                    rectangle.xOnAxis as Number,
                    (rectangle.yOnAxis - rectangle.heightOnAxis) as Number
                ).also {
                    it.XValueProperty().bind(rectangle.xOnAxisProperty.asObject())
                    it.YValueProperty()
                        .bind(rectangle.yOnAxisProperty.subtract(rectangle.heightOnAxisProperty).asObject())
                },
                Data(
                    (rectangle.xOnAxis + rectangle.widthOnAxis) as Number,
                    (rectangle.yOnAxis - rectangle.heightOnAxis) as Number
                ).also {
                    it.XValueProperty().bind(rectangle.xOnAxisProperty.add(rectangle.widthOnAxisProperty).asObject())
                    it.YValueProperty()
                        .bind(rectangle.yOnAxisProperty.subtract(rectangle.heightOnAxisProperty).asObject())
                }
            )
        )

        data += series.also {
            it.data.addListener(ListChangeListener { c ->
                while (c.next()) {
                    removeRectangle(rectangle)
                }
            })
        }

        _rectangleSeries += rectangle to series

        rectangle.viewOrder = series.data.maxOf { it.node.viewOrder } + 1
    }

    private fun removeRectangle(rectangle: Rectangle) {
        plotChildren.remove(rectangle)
        _rectangleSeries.removeAll { it.first == rectangle }
    }

    private fun removeSeriesFor(rectangle: Rectangle) {
        data.removeAll(_rectangleSeries.filter { it.first == rectangle }.map { it.second })
    }
}