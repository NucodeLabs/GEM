package ru.nucodelabs.gem.view.control.chart

import javafx.beans.NamedArg
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.canvas.Canvas
import javafx.scene.chart.ValueAxis
import javafx.scene.effect.BlendMode
import javafx.scene.image.Image
import javafx.scene.paint.Color
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.geo.ves.calc.interpolation.Interpolator
import ru.nucodelabs.geo.ves.calc.interpolation.InterpolatorContext
import ru.nucodelabs.kfx.ext.clear
import ru.nucodelabs.util.Point
import tornadofx.getValue
import tornadofx.setValue

class InterpolationMap @JvmOverloads constructor(
    @NamedArg("xAxis") xAxis: ValueAxis<Number>,
    @NamedArg("yAxis") yAxis: ValueAxis<Number>,
    @NamedArg("colorMapper") colorMapper: ColorMapper? = null
) : AbstractMap(xAxis, yAxis, colorMapper) {

    var canvasBlendMode: BlendMode by backgroundCanvas.blendModeProperty()

    private val _interpolateSeriesIndex = SimpleIntegerProperty(0)

    private lateinit var interpolatorContext: InterpolatorContext

    private var canBeInterpolated = true

    private var needRedraw = false

    private var snapshot: Image? = null

    fun interpolateSeriesIndexProperty() = _interpolateSeriesIndex
    var interpolateSeriesIndex
        get() = _interpolateSeriesIndex.get()
        set(value) = _interpolateSeriesIndex.set(value)

    init {
        interpolateSeriesIndexProperty().addListener { _, _, _ ->
            initInterpolator()
            redrawSnapshot()
        }
    }

    private lateinit var interpolator: Interpolator
    private var preparedData: List<List<Point>> = mutableListOf()

    override fun layoutPlotChildren() {
        super.layoutPlotChildren()
        if (needRedraw) redrawSnapshot() else layoutSnapshot()
    }

    override fun onColorMapperChange() = redrawSnapshot()

    private fun redrawSnapshot() {
        initInterpolator()
        needRedraw = false
        val render = Canvas(1920.0, 1080.0) // FIXME: Remove hardcoded
        draw(render)
        snapshot = render.snapshot(null, null)
        layoutSnapshot()
    }

    private fun layoutSnapshot() {
        backgroundCanvas.graphicsContext2D.drawImage(
            snapshot,
            0.0,
            0.0,
            backgroundCanvas.width,
            backgroundCanvas.height
        )
    }

    override fun dataItemAdded(series: Series<Number, Number>?, itemIndex: Int, item: Data<Number, Number>?) {
        super.dataItemAdded(series, itemIndex, item)
        needRedraw = true
    }

    override fun dataItemRemoved(item: Data<Number, Number>?, series: Series<Number, Number>?) {
        super.dataItemRemoved(item, series)
        needRedraw = true
    }

    override fun dataItemChanged(item: Data<Number, Number>?) {
        super.dataItemChanged(item)
        needRedraw = true
    }

    private fun draw(canvas: Canvas) {
        if (data.isEmpty() || data[interpolateSeriesIndex].data.isEmpty()) {
            canvas.clear()
            return
        }

        if (!canBeInterpolated) {
            canvas.clear()
            return
        }

        val pw = canvas.graphicsContext2D.pixelWriter
        val maxY = interpolatorContext.maxY()
        val minY = interpolatorContext.minY()
        val xCanvasScale = canvas.width / xAxis.width
        val yCanvasScale = canvas.height / yAxis.height
        for (x in 0..canvas.width.toInt()) {
            for (y in 0..canvas.height.toInt()) {
                val xValue = xAxis.getValueForDisplay(x.toDouble() / xCanvasScale).toDouble()
                var yValue = yAxis.getValueForDisplay(y.toDouble() / yCanvasScale).toDouble()
                if (xValue.isNaN() || yValue.isNaN()) {
                    continue
                }
                yValue = yValue.coerceIn(minY, maxY)
                val fValue = if (preparedData.size == 1) {
                    interpolator.getValue(yValue)
                } else {
                    interpolator.getValue(xValue, yValue)
                }
                val color = if (fValue != -1.0) colorMapper?.colorFor(fValue) else Color.WHITE
                pw.setColor(x, y, color)
            }
        }
    }

    private fun initInterpolator() {
        if (data.isEmpty()) {
            return
        }
        val series = data[interpolateSeriesIndex]
        if (series.data.isEmpty()) {
            return
        }
        val groupByX = series.data.map {
            Point(
                it.xValue.toDouble(),
                it.yValue.toDouble(),
                it.extraValue as Double
            )
        }.sortedBy { it.x }.groupBy { it.x }.values.toMutableList()

        try {
            groupByX[0] = groupByX[0].map { Point(x = .0, it.y, it.z) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        preparedData = groupByX
        interpolatorContext = InterpolatorContext(preparedData)
        if (interpolatorContext.getGrid()[0].size < 2) {
            canBeInterpolated = false
            return
        } else {
            canBeInterpolated = true
        }
        interpolator = Interpolator(interpolatorContext)
    }
}
