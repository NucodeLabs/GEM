package ru.nucodelabs.gem.view.control.chart

import javafx.beans.NamedArg
import javafx.beans.property.SimpleIntegerProperty
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

    private val _interpolateSeriesIndex = SimpleIntegerProperty(0)
    fun interpolateSeriesIndexProperty() = _interpolateSeriesIndex
    var interpolateSeriesIndex
        get() = _interpolateSeriesIndex.get()
        set(value) = _interpolateSeriesIndex.set(value)

    init {
        interpolateSeriesIndexProperty().addListener { _, _, _ ->
            initInterpolator()
            draw(canvas)
        }
    }

    private val _colorMapper = SimpleObjectProperty<ColorMapper?>(colorMapper)
    fun colorMapperProperty() = _colorMapper
    var colorMapper: ColorMapper?
        set(value) = _colorMapper.set(value)
        get() = _colorMapper.get()

    init {
        colorMapperProperty().addListener { _, _, new ->
            startListening(new)
            draw(canvas)
        }
        startListening(colorMapper)
    }

    private fun startListening(colorMapper: ColorMapper?) {
        colorMapper?.minValueProperty()?.addListener { _, _, _ -> draw(canvas) }
        colorMapper?.maxValueProperty()?.addListener { _, _, _ -> draw(canvas) }
        colorMapper?.numberOfSegmentsProperty()?.addListener { _, _, _ -> draw(canvas) }
    }

    private lateinit var interpolator: Interpolator
    private var preparedData: List<List<Data<Double, Double>>> = mutableListOf()

    fun interpolatedValueInPoint(x: Double, y: Double): Double = interpolator.getValue(x, y)

    override fun layoutPlotChildren() {
        super.layoutPlotChildren()
        if (::interpolator.isInitialized) {
            draw(canvas)
        }
    }

    override fun seriesAdded(series: Series<Number, Number>?, seriesIndex: Int) {
        super.seriesAdded(series, seriesIndex)
        if (seriesIndex == interpolateSeriesIndex) {
            initInterpolator()
        }
    }

    override fun seriesRemoved(series: Series<Number, Number>?) {
        super.seriesRemoved(series)
        if (data.getOrNull(interpolateSeriesIndex) == null) {
            canvas.clear()
        }
    }

    override fun dataItemAdded(series: Series<Number, Number>?, itemIndex: Int, item: Data<Number, Number>?) {
        super.dataItemAdded(series, itemIndex, item)
        if (series == data[interpolateSeriesIndex]) {
            initInterpolator()
        }
    }

    override fun dataItemRemoved(item: Data<Number, Number>?, series: Series<Number, Number>?) {
        super.dataItemRemoved(item, series)
        if (series == data[interpolateSeriesIndex]) {
            initInterpolator()
        }
    }

    override fun dataItemChanged(item: Data<Number, Number>?) {
        super.dataItemChanged(item)
        if (item in data[interpolateSeriesIndex].data) {
            initInterpolator()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun draw(canvas: Canvas) {
        if (data.isEmpty()) {
            canvas.clear()
            return
        }

        val pw = canvas.graphicsContext2D.pixelWriter
        for (x in 0..canvas.width.toInt()) {
            for (y in 0..canvas.height.toInt()) {
                val xValue = xAxis.getValueForDisplay(x.toDouble()).toDouble()
                val yValue = yAxis.getValueForDisplay(y.toDouble()).toDouble()
                if (xValue.isNaN() || yValue.isNaN()) {
                    continue
                }
                try {
                    val fValue = if (preparedData.size == 1)
                        interpolator.getValue(yValue)
                    else
                        interpolator.getValue(xValue, yValue)
                    val color = colorMapper?.colorFor(fValue)
                    pw.setColor(x, y, color)
                } catch (_: Exception) {
                    continue
                }
            }
        }
    }

    private fun initInterpolator() {
        val series = data[interpolateSeriesIndex]
        if (series.data.isEmpty()) {
            canvas.clear()
            return
        }
        val groupByX = series.data.sortedBy { it.xValue.toDouble() }.groupBy { it.xValue }.values.toMutableList()

        try {
            groupByX[0] = groupByX[0].map {
                Data(
                    0.0,
                    it.yValue,
                    it.extraValue
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        groupByX as List<List<Data<Double, Double>>>
        preparedData = groupByX
        interpolator = Interpolator(preparedData)
    }
}
