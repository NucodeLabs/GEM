package ru.nucodelabs.gem.view.control.chart

import javafx.application.Platform
import javafx.beans.NamedArg
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.canvas.Canvas
import javafx.scene.chart.ValueAxis
import javafx.scene.effect.BlendMode
import javafx.scene.layout.*
import ru.nucodelabs.gem.util.fx.*
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.gem.view.control.chart.ChartUtils.draw
import ru.nucodelabs.geo.ves.calc.interpolation.ApacheInterpolator2D
import ru.nucodelabs.geo.ves.calc.interpolation.RBFSpatialInterpolator
import ru.nucodelabs.geo.ves.calc.interpolation.SmartInterpolator

class CombinedChart @JvmOverloads constructor(
    @NamedArg("xAxis") private val xAxis: ValueAxis<Number>,
    @NamedArg("yAxis") private val yAxis: ValueAxis<Number>,
    @NamedArg("colorMapper") colorMapper: ColorMapper? = null,
) : ImageScatterChart(xAxis, yAxis) {

    private val _colorMapper = SimpleObjectProperty(colorMapper)
    private var interpolatorIsInitialized = false
    private fun colorMapperProperty(): ObjectProperty<ColorMapper?> = _colorMapper
    var colorMapper by _colorMapper
    val canvas: Canvas = Canvas(plotArea.width, plotArea.height)
    private val _blendMode = SimpleObjectProperty(canvas.blendMode)
    fun canvasBlendModeProperty(): ObjectProperty<BlendMode?> = _blendMode
    fun getCanvasBlendMode(): BlendMode? = canvasBlendModeProperty().get()
    fun setCanvasBlendMode(mode: BlendMode) = canvasBlendModeProperty().set(mode)
    var interpolator2D = SmartInterpolator(RBFSpatialInterpolator(), ApacheInterpolator2D())

    init {
        _blendMode.addListener { _, _, newBlendMode ->
            canvas.blendMode = newBlendMode
        }
        setCanvasBlendMode(BlendMode.SOFT_LIGHT)
        plotChildren += canvas
        canvas.layoutX = 0.0
        canvas.layoutY = 0.0
        canvas.widthProperty().bind(plotArea.widthProperty())
        canvas.heightProperty().bind(plotArea.heightProperty())
        canvas.viewOrder = 1.0
        colorMapperProperty().addListener { _, _, new ->
            ChartUtils.startListening(new) {
                draw()
            }
            draw()
        }
        ChartUtils.startListening(colorMapper) { draw() }
    }

    override fun layoutPlotChildren() {
        Platform.runLater {
            super.layoutPlotChildren()
            if (!interpolatorIsInitialized) {
                initInterpolator()
                interpolatorIsInitialized = true
            }
            draw()
        }
    }


    private fun initInterpolator() {
        ChartUtils.initInterpolator(data, interpolator2D)
    }

    fun draw() {
        draw(canvas, xAxis, yAxis, interpolator2D, colorMapper)
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
}