package ru.nucodelabs.gem.view.control.chart

import javafx.beans.NamedArg
import javafx.scene.chart.ValueAxis
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.geo.ves.calc.interpolation.ApacheInterpolator2D
import ru.nucodelabs.geo.ves.calc.interpolation.RBFSpatialInterpolator
import ru.nucodelabs.geo.ves.calc.interpolation.SmartInterpolator

// TODO: Optimize like InterpolationMap
class SmartInterpolationMap(
    @NamedArg("xAxis") xAxis: ValueAxis<Number>,
    @NamedArg("yAxis") yAxis: ValueAxis<Number>,
    @NamedArg("colorMapper") colorMapper: ColorMapper? = null
) : AbstractMap(xAxis, yAxis, colorMapper) {

    private var interpolatorIsInitialized = false
    private val interpolator2D = SmartInterpolator(RBFSpatialInterpolator(), ApacheInterpolator2D())

    override fun onColorMapperChange() = draw()

    override fun layoutPlotChildren() {
        super.layoutPlotChildren()
        if (!interpolatorIsInitialized) {
            initInterpolator()
            interpolatorIsInitialized = true
        }
        draw()
    }

    override fun dataItemAdded(series: Series<Number, Number>?, itemIndex: Int, item: Data<Number, Number>?) {
        super.dataItemAdded(series, itemIndex, item)
        interpolatorIsInitialized = false
    }

    override fun dataItemChanged(item: Data<Number, Number>?) {
        super.dataItemChanged(item)
        interpolatorIsInitialized = false
    }

    override fun dataItemRemoved(item: Data<Number, Number>?, series: Series<Number, Number>?) {
        super.dataItemRemoved(item, series)
        interpolatorIsInitialized = false
    }

    private fun initInterpolator() {
        initInterpolator(data, interpolator2D)
    }

    private fun draw() {
        draw(backgroundCanvas, xAxis, yAxis, interpolator2D, colorMapper)
    }
}