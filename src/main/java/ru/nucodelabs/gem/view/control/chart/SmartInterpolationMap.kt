package ru.nucodelabs.gem.view.control.chart

import javafx.beans.NamedArg
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ListChangeListener
import javafx.scene.chart.ValueAxis
import javafx.scene.paint.Color
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.geo.ves.calc.interpolation.ApacheInterpolator2D
import ru.nucodelabs.geo.ves.calc.interpolation.RBFSpatialInterpolator
import ru.nucodelabs.geo.ves.calc.interpolation.SmartInterpolator

class SmartInterpolationMap(
    @NamedArg("xAxis") xAxis: ValueAxis<Number>,
    @NamedArg("yAxis") yAxis: ValueAxis<Number>,
    @NamedArg("colorMapper") colorMapper: ColorMapper? = null
) : AbstractMap(xAxis, yAxis) {

    private val _colorMapper = SimpleObjectProperty(colorMapper)
    fun colorMapperProperty(): ObjectProperty<ColorMapper?> = _colorMapper
    var colorMapper: ColorMapper?
        set(value) = _colorMapper.set(value)
        get() = _colorMapper.get()

    init {
        colorMapperProperty().addListener { _, _, new ->
            startListening(new)
            draw()
        }
        startListening(colorMapper)
    }

    private fun startListening(colorMapper: ColorMapper?) {
        colorMapper?.minValueProperty()?.addListener { _, _, _ -> draw() }
        colorMapper?.maxValueProperty()?.addListener { _, _, _ -> draw() }
        colorMapper?.numberOfSegmentsProperty()?.addListener { _, _, _ -> draw() }
        colorMapper?.logScaleProperty()?.addListener { _, _, _ -> draw() }
    }


    private val interpolator2D = SmartInterpolator(RBFSpatialInterpolator(), ApacheInterpolator2D())

    override fun layoutPlotChildren() {
        super.layoutPlotChildren()
        draw()
    }

    override fun seriesAdded(series: Series<Number, Number>?, seriesIndex: Int) {
        super.seriesAdded(series, seriesIndex)
        initInterpolator()
    }

    override fun seriesChanged(c: ListChangeListener.Change<out Series<Any, Any>>?) {
        super.seriesChanged(c)
        initInterpolator()
    }

    override fun seriesRemoved(series: Series<Number, Number>?) {
        super.seriesRemoved(series)
        initInterpolator()
    }

    override fun dataItemAdded(series: Series<Number, Number>?, itemIndex: Int, item: Data<Number, Number>?) {
        super.dataItemAdded(series, itemIndex, item)
        initInterpolator()
    }

    override fun dataItemChanged(item: Data<Number, Number>?) {
        super.dataItemChanged(item)
        initInterpolator()
    }

    override fun dataItemRemoved(item: Data<Number, Number>?, series: Series<Number, Number>?) {
        super.dataItemRemoved(item, series)
        initInterpolator()
    }

    @Suppress("UNCHECKED_CAST")
    private fun initInterpolator() {
        interpolator2D.build(data.flatMap { it.data as List<Data<Double, Double>> })
    }

    fun draw() {
        for (x in 0 until canvas.width.toInt()) {
            for (y in 0 until canvas.height.toInt()) {
                val xValue = xAxis.getValueForDisplay(x.toDouble()).toDouble()
                val yValue = yAxis.getValueForDisplay(y.toDouble()).toDouble()
                canvas.graphicsContext2D.pixelWriter.run {
                    setColor(x, y, colorMapper?.colorFor(interpolator2D.getValue(xValue, yValue)) ?: Color.WHITE)
                }
            }
        }
    }
}