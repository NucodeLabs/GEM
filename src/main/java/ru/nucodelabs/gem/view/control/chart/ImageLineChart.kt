package ru.nucodelabs.gem.view.control.chart

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.chart.LineChart
import javafx.scene.chart.ValueAxis
import javafx.scene.image.Image
import javafx.scene.layout.*
import ru.nucodelabs.gem.util.fx.getValue
import ru.nucodelabs.gem.util.fx.minus
import ru.nucodelabs.gem.util.fx.plus
import ru.nucodelabs.gem.util.fx.setValue


class ImageLineChart(
    private val xAxis: ValueAxis<Number>,
    private val yAxis: ValueAxis<Number>,
    image: Image
) : LineChart<Number, Number>(xAxis, yAxis) {

    private val plotArea = this.lookup(".chart-plot-background") as Region
    private val _plotBackgroundProperty = plotArea.backgroundProperty()
    private var plotBackground: Background? by _plotBackgroundProperty

    private val _imageProperty: ObjectProperty<Image> = SimpleObjectProperty(image).apply {
        addListener { _, _, newImg: Image? ->
            newImg!!
            setupImage(newImg)
        }
    }

    var image: Image by _imageProperty
    fun imageProperty() = _imageProperty

    init {
        setupImage(image)
    }

    private fun setupImage(img: Image) {
        setImageAsBackground(image)
        bindChartSizeToImageSize(image)
    }

    private fun setImageAsBackground(img: Image) {
        plotBackground = Background(
            BackgroundImage(
                img,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT
            )
        )
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
}