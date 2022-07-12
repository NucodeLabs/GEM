package ru.nucodelabs.gem.view.control.chart

import javafx.beans.NamedArg
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.canvas.Canvas
import javafx.scene.chart.ValueAxis
import ru.nucodelabs.gem.view.color.ColorMapper

class InterpolationMap @JvmOverloads constructor(
    @NamedArg("xAxis") xAxis: ValueAxis<Number>,
    @NamedArg("yAxis") yAxis: ValueAxis<Number>,
    @NamedArg("colorMapper") colorMapper: ColorMapper? = null
) : AbstractMap(xAxis, yAxis) {

    private val _colorMapper = SimpleObjectProperty<ColorMapper?>(colorMapper)
    fun colorMapperProperty() = _colorMapper
    var colorMapper: ColorMapper?
        set(value) = _colorMapper.set(value)
        get() = _colorMapper.get()

    @Suppress("UNCHECKED_CAST")
    override fun draw(canvas: Canvas) {
//        if (data.isEmpty()) {
//            canvas.clear()
//            return
//        }
//
//        for (series in data) {
//            val groupByX = series.data.sortedBy { it.xValue.toDouble() }.groupBy { it.xValue }.values.toMutableList()
//
//            try {
//                groupByX[0] = groupByX[0].map {
//                    Data(
//                        0.0,
//                        it.yValue,
//                        it.extraValue
//                    )
//                }
//
//                PseudoInterpolator(groupByX as List<List<Data<Double, Double>>>, colorMapper).paint(canvas)
//                // safe cast Double : Number
//            } catch (e: Exception) {
//                throw RuntimeException(e)
//            }
//        }
    }
}