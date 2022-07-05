package ru.nucodelabs.gem.extensions.fx

import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import java.util.*

fun ObservableList<*>.emptyBinding(): BooleanBinding = Bindings.createBooleanBinding({ isEmpty() }, this)

operator fun ResourceBundle.get(key: String): String = this.getString(key)

fun <T> List<T>.toObservableList(): ObservableList<T> = FXCollections.observableList(this)

fun <T> observableListOf(vararg items: T): ObservableList<T> = FXCollections.observableList(mutableListOf(*items))