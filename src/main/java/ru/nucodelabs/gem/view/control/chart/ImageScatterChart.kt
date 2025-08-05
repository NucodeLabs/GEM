package ru.nucodelabs.gem.view.control.chart

import javafx.beans.NamedArg
import javafx.beans.binding.Bindings
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.chart.ScatterChart
import javafx.scene.chart.ValueAxis
import javafx.scene.image.Image
import javafx.scene.layout.*
import javafx.scene.paint.Color
import ru.nucodelabs.kfx.ext.generateImage
import tornadofx.getValue
import tornadofx.minus
import tornadofx.plus
import tornadofx.setValue


open class ImageScatterChart @JvmOverloads constructor(
    @NamedArg("xAxis") xAxis: ValueAxis<Number>,
    @NamedArg("yAxis") yAxis: ValueAxis<Number>,
    @NamedArg("image") image: Image = generateImage(256, 256, Color.WHITESMOKE)
) : ScatterChart<Number, Number>(xAxis, yAxis) {

    protected val plotArea = this.lookup(".chart-plot-background") as Region
    private val _plotBackgroundProperty = plotArea.backgroundProperty()

    private val _imageProperty: ObjectProperty<Image> = SimpleObjectProperty(image).apply {
        addListener { _, _, newImg: Image? ->
            setupImage(newImg ?: generateImage(256, 256, Color.WHITE))
        }
    }

    var image: Image? by _imageProperty
    fun imageProperty() = _imageProperty

    init {
        _plotBackgroundProperty.bind(
            Bindings.createObjectBinding(
                {
                    imageToBackground()
                }, _imageProperty
            )
        )
        setupImage(image)
    }

    private fun setupImage(img: Image) {
        bindChartSizeToImageSize(img)
        layoutChildren()
    }

    private fun imageToBackground(): Background? {
        return if (image != null) {
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