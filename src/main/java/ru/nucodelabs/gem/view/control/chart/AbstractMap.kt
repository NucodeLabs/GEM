package ru.nucodelabs.gem.view.control.chart

import javafx.beans.NamedArg
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.canvas.Canvas
import javafx.scene.chart.ScatterChart
import javafx.scene.chart.ValueAxis
import javafx.scene.layout.Region
import ru.nucodelabs.gem.view.color.ColorMapper

abstract class AbstractMap(
    @NamedArg("xAxis") xAxis: ValueAxis<Number>,
    @NamedArg("yAxis") yAxis: ValueAxis<Number>,
    @NamedArg("colorMapper") colorMapper: ColorMapper? = null
) : ScatterChart<Number, Number>(xAxis, yAxis) {

    private val _colorMapper = SimpleObjectProperty<ColorMapper?>(colorMapper)
    fun colorMapperProperty() = _colorMapper
    var colorMapper: ColorMapper?
        set(value) = _colorMapper.set(value)
        get() = _colorMapper.get()

    init {
        colorMapperProperty().addListener { _, _, new ->
            startListening(new)
            onColorMapperChange()
        }
        startListening(colorMapper)
    }

    private fun startListening(colorMapper: ColorMapper?) {
        colorMapper?.minValueProperty()?.addListener { _, _, _ -> onColorMapperChange() }
        colorMapper?.maxValueProperty()?.addListener { _, _, _ -> onColorMapperChange() }
        colorMapper?.numberOfSegmentsProperty()?.addListener { _, _, _ -> onColorMapperChange() }
        colorMapper?.logScaleProperty()?.addListener { _, _, _ -> onColorMapperChange() }
    }

    protected val plotBackground = this.lookup(".chart-plot-background") as Region

    protected val backgroundCanvas: Canvas = Canvas(plotBackground.width, plotBackground.height)

    init {
        plotChildren += backgroundCanvas
        backgroundCanvas.layoutX = 0.0
        backgroundCanvas.layoutY = 0.0
        backgroundCanvas.widthProperty().bind(plotBackground.widthProperty())
        backgroundCanvas.heightProperty().bind(plotBackground.heightProperty())
        backgroundCanvas.managedProperty().bind(this.managedProperty())
        backgroundCanvas.viewOrder = 1.0
    }

    protected abstract fun onColorMapperChange()
}