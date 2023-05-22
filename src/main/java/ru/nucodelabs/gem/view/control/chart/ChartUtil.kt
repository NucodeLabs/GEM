package ru.nucodelabs.gem.view.control.chart

import javafx.collections.ObservableList
import javafx.scene.canvas.Canvas
import javafx.scene.chart.Axis
import javafx.scene.chart.XYChart
import javafx.scene.paint.Color
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.geo.ves.calc.interpolation.SmartInterpolator

object ChartUtil {
    fun startListening(colorMapper: ColorMapper?, draw: () -> Unit) {
        colorMapper?.minValueProperty()?.addListener { _, _, _ -> draw() }
        colorMapper?.maxValueProperty()?.addListener { _, _, _ -> draw() }
        colorMapper?.numberOfSegmentsProperty()?.addListener { _, _, _ -> draw() }
        colorMapper?.logScaleProperty()?.addListener { _, _, _ -> draw() }
    }

    @Suppress("UNCHECKED_CAST")
    fun initInterpolator(data: ObservableList<XYChart.Series<Number, Number>>, interpolator2D: SmartInterpolator) {
        if (!data.isEmpty()) {
            interpolator2D.build(data.flatMap { it.data as List<XYChart.Data<Double, Double>> })
        }
    }

    fun draw(canvas: Canvas, xAxis: Axis<Number>, yAxis: Axis<Number>, interpolator2D: SmartInterpolator, colorMapper: ColorMapper?) {
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

