package ru.nucodelabs.kfx.ext

import javafx.beans.Observable
import javafx.beans.binding.Bindings.createBooleanBinding
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.BooleanProperty
import javafx.beans.property.Property
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.SnapshotParameters
import javafx.scene.canvas.Canvas
import javafx.scene.chart.XYChart
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter.Change
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.scene.transform.Transform
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.util.Duration
import javafx.util.StringConverter
import ru.nucodelabs.kfx.pref.Preference
import java.io.File
import java.lang.String.format
import java.text.DecimalFormat
import java.text.ParsePosition
import java.util.*
import java.util.function.UnaryOperator
import java.util.prefs.Preferences
import javax.imageio.ImageIO
import kotlin.math.ceil


typealias Line<X, Y> = XYChart.Series<X, Y>

typealias Point<X, Y> = XYChart.Data<X, Y>

/**
 * Returns binding that tells if list empty
 */
fun ObservableList<*>.emptyBinding(): BooleanBinding = createBooleanBinding({ isEmpty() }, this)

/**
 * Same as `getString()`
 */
operator fun ResourceBundle.get(key: String): String = this.getString(key)

/**
 * Returns new mutable observable list with elements of this
 */
fun <T> List<T>.toObservableList(): ObservableList<T> = FXCollections.observableList(this.toMutableList())

/**
 * Returns new mutable observable list with items
 */
fun <T> observableListOf(vararg items: T): ObservableList<T> = FXCollections.observableList(mutableListOf(*items))

/**
 * Returns CSS color string `rgba(r, g, b, a)`
 * where `r`, `g`, `b` is integer representation, and `a` is float 0.0-1.0
 */
fun Color.toCss(): String = format(
    "rgba(%d, %d, %d, %f)",
    ceil(red * 255).toInt(),
    ceil(green * 255).toInt(),
    ceil(blue * 255).toInt(),
    opacity
)

fun TextField.isValidBy(
    styleIfInvalid: String = "-fx-background-color: LightPink",
    blankIsValid: Boolean = true,
    validate: (String) -> Boolean,
): ReadOnlyBooleanProperty {
    val property = SimpleBooleanProperty()

    fun run() {
        if (text.isBlank()) {
            property.set(blankIsValid)
            style = ""
        } else {
            if (!validate(text)) {
                property.set(false)
                style = styleIfInvalid
            } else {
                property.set(true)
                style = ""
            }
        }
    }
    run()

    textProperty().addListener { _, _, _ -> run() }

    return property
}

/**
 * Returns boolean binding that tells if string is blank
 */
fun ObservableValue<String>.isBlank(): BooleanBinding = createBooleanBinding({ value.isBlank() }, this)

/**
 * Returns boolean binding that tells if string is not blank
 */
fun ObservableValue<String>.isNotBlank(): BooleanBinding = isBlank().not()

fun BooleanProperty.bidirectionalNot(): BooleanProperty = SimpleBooleanProperty(!value).also {
    it.addListener { _, _, new -> this.set(!new) }
    this.addListener { _, _, new -> it.set(!new) }
}

/**
 * Creates series with 2 data points
 */
fun <X, Y> line(point1: Point<X, Y>, point2: Point<X, Y>): Line<X, Y> = Line(observableListOf(point1, point2))

/**
 * Returns array of all bounds properties of nodes, so it can be passed in `create*Binding()` function as dependencies
 */
fun Node.sizeObservables(): Array<Observable> = arrayOf(
    layoutBoundsProperty(),
    boundsInParentProperty(),
    boundsInLocalProperty()
)

/**
 * Clears whole canvas
 */
fun Canvas.clear() = graphicsContext2D.clearRect(0.0, 0.0, width, height)

val Label.textNode: Text
    get() = childrenUnmodifiable.filterIsInstance<Text>().first()

/**
 * Flips node horizontally by multiplying its scale by -1
 */
fun Node.flipHorizontally() {
    scaleX *= -1.0
}

/**
 * Flips node vertically by multiplying its scale by -1
 */
fun Node.flipVertically() {
    scaleY *= -1.0
}

/**
 * Allow only decimal numbers output
 */
fun decimalFilter(decimalFormat: DecimalFormat) = UnaryOperator<Change> { c ->
    if (c.controlNewText.isEmpty()) {
        return@UnaryOperator c
    }

    val parsePosition = ParsePosition(0)
    val trimmed = c.controlNewText.trim().replace(" ", "")
    if (trimmed == "-") {
        return@UnaryOperator c
    }
    val obj = decimalFormat.parse(trimmed, parsePosition)

    if (obj == null || parsePosition.index < trimmed.length) {
        null
    } else {
        c
    }
}

/**
 * Allows only integer input
 */
fun intFilter() = UnaryOperator<Change?> { c ->
    c!!
    if (c.controlNewText.isEmpty() || c.controlNewText == "-") {
        return@UnaryOperator c
    }
    c.controlNewText.toIntOrNull()?.let { c }
}

class DoubleValidationConverter(
    private val decimalFormat: DecimalFormat = DecimalFormat(),
    private val validate: (Double) -> Boolean = { true }
) : StringConverter<Double>() {
    override fun toString(o: Double?): String = decimalFormat.format(o)
    override fun fromString(string: String?): Double =
        decimalFormat.parse(string).toDouble().takeIf(validate) ?: throw IllegalArgumentException()
}

class IntValidationConverter(private val validate: (Int) -> Boolean = { true }) : StringConverter<Int>() {
    override fun toString(o: Int?): String = o?.toString() ?: ""
    override fun fromString(string: String?): Int =
        string?.toInt()?.takeIf(validate) ?: throw IllegalArgumentException()
}

fun Tooltip.noDelay() = apply { showDelay = Duration.ZERO }

fun Tooltip.noAutoHide() = apply { showDuration = Duration.INDEFINITE }

fun Tooltip.noHideDelay() = apply { hideDelay = Duration.ZERO }

fun Tooltip.forCharts() = this.noDelay().noAutoHide().noHideDelay()

/**
 * As PNG
 */
fun Region.saveSnapshotAsPng(fileChooser: FileChooser = FileChooser(), scale: Double = 10.0): File? {
    val file = fileChooser.showSaveDialog(scene?.window)
    if (file != null) {
        val snapshot: WritableImage = snapshot(
            SnapshotParameters().also {
                it.transform = Transform.scale(scale, scale)
            },
            null
        )
        ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file)
    }
    return file
}

fun generateImage(width: Int, height: Int, color: Color): Image {
    val writableImg = WritableImage(width, height)
    val pxWriter = writableImg.pixelWriter

    for (x in 0 until width) {
        for (y in 0 until height) {
            pxWriter.setColor(x, y, color)
        }
    }

    return writableImg
}

/**
 * Binds this property to an observable, automatically unbinding it before if already bound.
 */
infix fun <T> Property<T>.bindTo(observable: ObservableValue<T>) {
    unbind()
    bind(observable)
}

fun currentWindow(): Stage = Stage.getWindows().find { it.isFocused } as Stage

fun saveInitialDirectory(
    preferences: Preferences,
    initDirPref: Preference<String>,
    fileChooser: FileChooser,
    file: File?,
) {
    if (file != null && file.parentFile.isDirectory) {
        fileChooser.initialDirectory = file.parentFile
        preferences.put(initDirPref.key, file.parentFile.absolutePath)
    }
}

/**
 * Move focus to parent node
 */
fun unfocus(node: Node) {
    if (node.isFocused) {
        node.parent.requestFocus()
    }
}

fun switch(from: Parent, to: Parent) {
    hide(from)
    show(to)
    to.requestLayout()
    to.layout()
}

fun hide(node: Node) {
    node.isVisible = false
    node.isManaged = false
}

fun show(node: Node) {
    node.isManaged = true
    node.isVisible = true
}