package ru.nucodelabs.gem.extensions.fx

import javafx.beans.binding.Bindings
import javafx.beans.binding.Bindings.createBooleanBinding
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.chart.XYChart
import javafx.scene.control.TextField
import javafx.scene.paint.Color
import java.lang.String.format
import java.util.*
import kotlin.math.ceil

typealias Line<X, Y> = XYChart.Series<X, Y>

typealias Point<X, Y> = XYChart.Data<X, Y>

fun ObservableList<*>.emptyBinding(): BooleanBinding = Bindings.createBooleanBinding({ isEmpty() }, this)

operator fun ResourceBundle.get(key: String): String = this.getString(key)

fun <T> List<T>.toObservableList(): ObservableList<T> = FXCollections.observableList(this.toMutableList())

fun <T> observableListOf(vararg items: T): ObservableList<T> = FXCollections.observableList(mutableListOf(*items))

fun Color.toCss(): String = format(
    "rgba(%d, %d, %d, %f)",
    ceil(red * 255).toInt(),
    ceil(green * 255).toInt(),
    ceil(blue * 255).toInt(),
    opacity
)

fun TextField.isValidBy(
    styleIfInvalid: String = "-fx-background-color: LightPink",
    validate: (String) -> Boolean
): ReadOnlyBooleanProperty {
    val property = SimpleBooleanProperty()

    fun run() {
        if (!validate(text)) {
            property.set(false)
            style = styleIfInvalid
        } else {
            property.set(true)
            style = ""
        }
    }
    run()

    textProperty().addListener { _, _, _ -> run() }

    return property
}

fun ReadOnlyStringProperty.isBlank(): BooleanBinding = createBooleanBinding({ value.isBlank() }, this)

fun ReadOnlyStringProperty.isNotBlank(): BooleanBinding = isBlank().not()

fun <X, Y> line(point1: Point<X, Y>, point2: Point<X, Y>): Line<X, Y> = Line(observableListOf(point1, point2))