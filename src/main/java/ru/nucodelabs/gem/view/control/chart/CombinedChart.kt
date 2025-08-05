package ru.nucodelabs.gem.view.control.chart

import javafx.beans.NamedArg
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.canvas.Canvas
import javafx.scene.chart.ValueAxis
import javafx.scene.effect.BlendMode
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.geo.ves.calc.interpolation.ApacheInterpolator2D
import ru.nucodelabs.geo.ves.calc.interpolation.RBFSpatialInterpolator
import ru.nucodelabs.geo.ves.calc.interpolation.SmartInterpolator
import tornadofx.getValue
import tornadofx.setValue

/**
 * График с двумя слоями,
 * верхний -- canvas, нижний -- image
 */
class CombinedChart @JvmOverloads constructor(
    @NamedArg("xAxis") xAxis: ValueAxis<Number>,
    @NamedArg("yAxis") yAxis: ValueAxis<Number>,
    @NamedArg("colorMapper") colorMapper: ColorMapper? = null,
) : ImageScatterChart(xAxis, yAxis) {

    private var interpolator2D = SmartInterpolator(RBFSpatialInterpolator(), ApacheInterpolator2D())

    private val _colorMapper: ObjectProperty<ColorMapper?> = SimpleObjectProperty(null)
    private var interpolatorIsInitialized = false
    private fun colorMapperProperty(): ObjectProperty<ColorMapper?> = _colorMapper
    var colorMapper by _colorMapper
    val canvas: Canvas = Canvas(plotArea.width, plotArea.height)

    fun canvasBlendModeProperty(): ObjectProperty<BlendMode?> = canvas.blendModeProperty()
    var canvasBlendMode: BlendMode? by canvasBlendModeProperty()

    fun canvasOpacityProperty(): DoubleProperty = canvas.opacityProperty()
    var canvasOpacity: Double by canvasOpacityProperty()

    init {
        plotChildren += canvas
        canvas.layoutX = 0.0
        canvas.layoutY = 0.0
        canvas.widthProperty().bind(plotArea.widthProperty())
        canvas.heightProperty().bind(plotArea.heightProperty())
        canvas.viewOrder = 1.0
        colorMapperProperty().addListener { _, _, new ->
            ChartUtil.startListening(new) {
                draw()
            }
            draw()
        }
        ChartUtil.startListening(colorMapper) { draw() }
    }

    override fun layoutPlotChildren() {
        super.layoutPlotChildren()
        if (!interpolatorIsInitialized) {
            initInterpolator()
            interpolatorIsInitialized = true
        }
        draw()
    }


    private fun initInterpolator() {
        ChartUtil.initInterpolator(data, interpolator2D)
    }

    private fun draw() {
        ChartUtil.draw(canvas, xAxis, yAxis, interpolator2D, colorMapper)
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