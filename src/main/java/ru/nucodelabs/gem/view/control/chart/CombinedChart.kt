package ru.nucodelabs.gem.view.control.chart

import javafx.beans.NamedArg
import javafx.beans.binding.Bindings
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.canvas.Canvas
import javafx.scene.chart.ScatterChart
import javafx.scene.chart.ValueAxis
import javafx.scene.effect.BlendMode
import javafx.scene.image.Image
import javafx.scene.layout.*
import javafx.scene.paint.Color
import ru.nucodelabs.gem.util.fx.*
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.geo.ves.calc.interpolation.ApacheInterpolator2D
import ru.nucodelabs.geo.ves.calc.interpolation.RBFSpatialInterpolator
import ru.nucodelabs.geo.ves.calc.interpolation.SmartInterpolator

class CombinedChart @JvmOverloads constructor(
    @NamedArg("xAxis") private val xAxis: ValueAxis<Number>,
    @NamedArg("yAxis") private val yAxis: ValueAxis<Number>,
    @NamedArg("colorMapper") colorMapper: ColorMapper? = null,
    image: Image = generateImage(256, 256, Color.WHITESMOKE)
) : ScatterChart<Number, Number>(xAxis, yAxis) {

    private val _colorMapper = SimpleObjectProperty(colorMapper)
    private var interpolatorIsInitialized = false
    fun colorMapperProperty(): ObjectProperty<ColorMapper?> = _colorMapper
    var colorMapper: ColorMapper?
        set(value) = _colorMapper.set(value)
        get() = _colorMapper.get()
    private val plotArea = this.lookup(".chart-plot-background") as Region
    private val _plotBackgroundProperty = plotArea.backgroundProperty()
    val canvas: Canvas = Canvas(plotArea.width, plotArea.height)

    private val _imageProperty: ObjectProperty<Image> = SimpleObjectProperty(image).apply {
        addListener { _, _, newImg: Image? ->
            setupImage(newImg ?: generateImage(256, 256, Color.WHITE))
        }
    }

    var image: Image by _imageProperty
    fun imageProperty() = _imageProperty


    init {
        canvas.blendMode = BlendMode.SOFT_LIGHT
        plotChildren += canvas
        canvas.layoutX = 0.0
        canvas.layoutY = 0.0
        canvas.widthProperty().bind(plotArea.widthProperty())
        canvas.heightProperty().bind(plotArea.heightProperty())
        canvas.viewOrder = 1.0
        _plotBackgroundProperty.bind(
            Bindings.createObjectBinding(
                {
                    imageToBackground()
                }, _imageProperty
            )
        )
        setupImage(image)
        colorMapperProperty().addListener { _, _, new ->
            startListening(new)
            draw()
        }
        startListening(colorMapper)
    }

    private fun setupImage(img: Image) {
        bindChartSizeToImageSize(img)
        layoutChildren()
        layoutPlotChildren()
    }

    private fun imageToBackground(): Background? {
        return if (_imageProperty.get() != null) {
            Background(
                BackgroundImage(
                    image,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT
                )
            )
        } else {
            null
        }
    }

    fun setAxisRange(xLower: Double, xUpper: Double, yLower: Double, yUpper: Double) {
        xAxis.isAutoRanging = false
        xAxis.upperBound = xUpper
        xAxis.lowerBound = xLower

        yAxis.isAutoRanging = false
        yAxis.upperBound = yUpper
        yAxis.lowerBound = yLower
    }

    private fun bindChartSizeToImageSize(img: Image) {
        val xOffset = this.widthProperty() - plotArea.widthProperty()
        val newWidth = img.width + xOffset.value
        val newWidthBinding = img.widthProperty() + xOffset

        this.minWidthProperty().unbind()
        this.maxWidthProperty().unbind()
        this.prefWidthProperty().unbind()

        this.minWidth = newWidth
        this.minWidthProperty().bind(newWidthBinding)

        this.maxWidth = newWidth
        this.maxWidthProperty().bind(newWidthBinding)

        this.prefWidth = newWidth
        this.prefWidthProperty().bind(newWidthBinding)


        val yOffset = this.heightProperty() - plotArea.heightProperty()
        val newHeight = img.width + yOffset.value
        val newHeightBinding = img.heightProperty() + yOffset

        this.prefHeightProperty().unbind()
        this.minHeightProperty().unbind()
        this.maxHeightProperty().unbind()

        this.prefHeight = newHeight
        this.prefHeightProperty().bind(newHeightBinding)

        this.minHeight = newHeight
        this.minHeightProperty().bind(newHeightBinding)

        this.maxHeight = newHeight
        this.maxHeightProperty().bind(newHeightBinding)
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

    @Suppress("UNCHECKED_CAST")
    private fun initInterpolator() {
        //TODO: поставить нормальную проверку на корректность data для build
        if (!data.isEmpty()) {
            interpolator2D.build(data.flatMap { it.data as List<Data<Double, Double>> })
        }
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