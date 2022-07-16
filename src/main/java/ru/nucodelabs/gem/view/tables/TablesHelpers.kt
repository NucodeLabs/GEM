package ru.nucodelabs.gem.view.tables

import javafx.beans.binding.Bindings
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.util.Callback


const val DEFAULT_FONT_SIZE = 13
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
 * Validating string containing index for list/array
 * @return true if string represents valid index
 */

fun String.isIndexOrBlank() =
    takeIf { it.isBlank() }?.let { true } ?: toIntOrNull()?.takeIf { it >= 0 }?.let { true } ?: false

fun String.isIndex() = toIntOrNull()?.takeIf { it >= 0 }?.let { true } ?: false