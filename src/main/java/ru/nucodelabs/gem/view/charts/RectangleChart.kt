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
    }

    private val _rectangleSeries: MutableMap<Rectangle, Series<Number, Number>> = mutableMapOf()
    val dataRectangles: ObservableList<Rectangle> = observableListOf()

    init {
        dataRectangles.addListener(ListChangeListener { change ->
            if (change.next()) {
                for (rect in change.list) {
                    if (rect !in plotChildren) {
                        setupRectangle(rect)
                    }
                }

                for (rect in plotChildren.filterIsInstance<Rectangle>()) {
                    if (rect !in change.list) {
                        removeRectangle(rect)
                    }
                }
            }
        })
    }

    /**
     * Rectangles and associated series
     */
    val rectangleSeries
        get() = _rectangleSeries

    private val sizeProperties
        get() = arrayOf(
            layoutBoundsProperty(),
            boundsInParentProperty(),
            boundsInLocalProperty()
        )

    private fun setupRectangle(rectangle: Rectangle) {
        rectangle.x = xAxis.getDisplayPosition(rectangle.xOnAxis)
        rectangle.xProperty().bind(
            createDoubleBinding(
                { xAxis.getDisplayPosition(rectangle.xOnAxis) },
                rectangle.xOnAxisProperty,
                *sizeProperties
            )
        )

        rectangle.y = yAxis.getDisplayPosition(rectangle.yOnAxis + rectangle.heightOnAxis)
        rectangle.yProperty().bind(
            createDoubleBinding(
                { yAxis.getDisplayPosition(rectangle.yOnAxis + rectangle.heightOnAxis) },
                rectangle.yOnAxisProperty,
                rectangle.heightOnAxisProperty,
                *sizeProperties
            )
        )

        rectangle.width = xAxis.getDisplayPosition(rectangle.widthOnAxis)
        rectangle.widthProperty().bind(
            createDoubleBinding(
                { xAxis.getDisplayPosition(rectangle.widthOnAxis) },
                rectangle.widthOnAxisProperty,
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
                    (rectangle.yOnAxis + rectangle.heightOnAxis) as Number
                ).also {
                    it.XValueProperty().bind(rectangle.xOnAxisProperty.asObject())
                    it.YValueProperty().bind(rectangle.yOnAxisProperty.add(rectangle.heightOnAxisProperty).asObject())
                },
                Data(
                    (rectangle.xOnAxis + rectangle.widthOnAxis) as Number,
                    (rectangle.yOnAxis + rectangle.heightOnAxis) as Number
                ).also {
                    it.XValueProperty().bind(rectangle.xOnAxisProperty.add(rectangle.widthOnAxisProperty).asObject())
                    it.YValueProperty().bind(rectangle.yOnAxisProperty.add(rectangle.heightOnAxisProperty).asObject())
                }
            )
        )

        data += series

        _rectangleSeries[rectangle] = series

        plotChildren += rectangle

        rectangle.viewOrder = series.data.maxOf { it.node.viewOrder } + 1
    }

    private fun removeRectangle(rectangle: Rectangle) {
        plotChildren.remove(rectangle)
        data.removeAll(_rectangleSeries[rectangle])
        _rectangleSeries.remove(rectangle)
    }
}