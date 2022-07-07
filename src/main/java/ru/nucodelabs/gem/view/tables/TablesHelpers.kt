package ru.nucodelabs.gem.view.tables

import javafx.beans.binding.Bindings
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.util.Callback
import java.text.DecimalFormat
import java.text.ParseException


fun indexCellFactory(fromOne: Boolean = true): Callback<TableColumn<Any?, Int?>, TableCell<Any, Int>> =
    Callback { _: TableColumn<Any?, Int?> ->
        TableCell<Any, Int>().also {
            it.textProperty().bind(
                Bindings.`when`(it.emptyProperty())
                    .then("")
                    .otherwise(it.indexProperty().add(if (fromOne) 1 else 0).asString())
            )
        }
    }

/**
 * Validating string containing index in array
 *
 * @param s string
 * @return true if string represents valid index
 */
fun validateIndexInput(s: String): Boolean {
    return if (s.isBlank()) {
        true
    } else try {
        val number = s.toInt()
        number >= 0
    } catch (e: NumberFormatException) {
        false
    }
}

/**
 * Validating string containing double value
 *
 * @param s string
 * @return true if string represents valid double value
 */
fun validateDoubleInput(s: String, decimalFormat: DecimalFormat): Boolean {
    return if (s.isBlank()) {
        true
    } else try {
        decimalFormat.parse(s).toDouble()
        true
    } catch (e: ParseException) {
        false
    }
}

fun MutableList<*>.removeAllAt(indices: Collection<Int>) {
    for (index in indices.sorted().reversed()) {
        this.removeAt(index)
    }
}