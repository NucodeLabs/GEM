package ru.nucodelabs.gem.view.control.chart

import javafx.beans.NamedArg
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.canvas.Canvas
import javafx.scene.chart.ValueAxis
import javafx.scene.paint.Color
import javafx.util.StringConverter
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.kfx.ext.clear
import tornadofx.getValue
import tornadofx.setValue

class CanvasRenderPolygonChart @JvmOverloads constructor(
    @NamedArg("xAxis") xAxis: ValueAxis<Number>,
    @NamedArg("yAxis") yAxis: ValueAxis<Number>,
    @NamedArg("colorMapper") colorMapper: ColorMapper? = null,
) : AbstractMap(xAxis, yAxis, colorMapper) {

    init {
        animated = false
    }

    private val extraValueFormatterProperty = SimpleObjectProperty<StringConverter<Number>?>(null).apply {
        addListener { _, old, new -> render(backgroundCanvas) }
    }
    var extraValueFormatter: StringConverter<Number>? by extraValueFormatterProperty

    @Suppress("unused")
    fun extraValueFormatterProperty(): ObjectProperty<StringConverter<Number>?> = extraValueFormatterProperty

    private val extraValueVisibleProperty = SimpleBooleanProperty(false).apply {
        addListener { _, old, new -> render(backgroundCanvas) }
    }
    var extraValueVisible by extraValueVisibleProperty
    fun extraValueVisibleProperty(): BooleanProperty = extraValueVisibleProperty

    override fun onColorMapperChange() = render(backgroundCanvas)

    override fun layoutPlotChildren() {
        super.layoutPlotChildren()
        render(backgroundCanvas)
    }

    private fun render(canvas: Canvas) {
        val graphics = canvas.graphicsContext2D
        canvas.clear()
        data.forEach { series ->
            if (series.data.isEmpty()) return@forEach
            val (xPoints, yPoints) = toPolygon(series)
            val extraValue = series.data.first().extraValue as Number
            val color = colorMapper?.colorFor(extraValue.toDouble()) ?: Color.WHITE
            graphics.fill = color
            graphics.fillPolygon(xPoints, yPoints, xPoints.size)
            if (extraValueVisible) {
                val text = extraValueFormatter?.toString(extraValue) ?: extraValue.toString()
                val textX = xPoints.min()
                val textY = yPoints.max()
                graphics.fill = color.invert()
                graphics.fillText(text, textX, textY)
            }
        }
    }

    private fun toPolygon(series: Series<Number, Number>): Pair<DoubleArray, DoubleArray> {
        val xPoints = series.data.map { xAxis.getDisplayPosition(it.xValue) }.toDoubleArray()
        val yPoints = series.data.map { yAxis.getDisplayPosition(it.yValue) }.toDoubleArray()
        return Pair(xPoints, yPoints)
    }
}