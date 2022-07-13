package ru.nucodelabs.gem.view.control.chart

import javafx.beans.NamedArg
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.canvas.Canvas
import javafx.scene.chart.ValueAxis
import ru.nucodelabs.algorithms.interpolation.Interpolator
import ru.nucodelabs.gem.extensions.fx.clear
import ru.nucodelabs.gem.view.color.ColorMapper

class InterpolationMap @JvmOverloads constructor(
    @NamedArg("xAxis") xAxis: ValueAxis<Number>,
    @NamedArg("yAxis") yAxis: ValueAxis<Number>,
    @NamedArg("colorMapper") colorMapper: ColorMapper? = null
) : AbstractMap(xAxis, yAxis) {

    private val _colorMapper = SimpleObjectProperty<ColorMapper?>(colorMapper)
    //fun colorMapperProperty() = _colorMapper
    var colorMapper: ColorMapper?
        set(value) = _colorMapper.set(value)
        get() = _colorMapper.get()

    @Suppress("UNCHECKED_CAST")
    override fun draw(canvas: Canvas) {
        if (data.isEmpty()) {
            canvas.clear()
            return
        }
        var section: List<List<Data<Double, Double>>> = arrayListOf()
        for (series in data) {
            val groupByX = series.data.sortedBy { it.xValue.toDouble() }.groupBy { it.xValue }.values.toMutableList()

            try {
                groupByX[0] = groupByX[0].map {
                    Data(
                        0.0,
                        it.yValue,
                        it.extraValue
                    )
                }
                section = groupByX as List<List<Data<Double, Double>>>

            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
        val interpolator = Interpolator(section)
        val pw = canvas.graphicsContext2D.pixelWriter
        for (x in 0..canvas.width.toInt()) {
            for (y in 0..canvas.height.toInt()) {
                val xValue = xAxis.getValueForDisplay(x.toDouble()).toDouble()
                val yValue = yAxis.getValueForDisplay(y.toDouble()).toDouble()
                if (yValue < 1.5) continue
                val fValue = if (section.size == 1)
                    interpolator.getValue(yValue)
                else
                    interpolator.getValue(xValue, yValue)
                val color = colorMapper?.colorFor(fValue)
                pw.setColor(x, y, color)
            }
        }
    }
}
