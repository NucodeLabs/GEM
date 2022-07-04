package ru.nucodelabs.gem.extensions.fx

import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.collections.ObservableList
import java.util.*

fun ObservableList<*>.emptyBinding(): BooleanBinding = Bindings.createBooleanBinding({ isEmpty() }, this)

operator fun ResourceBundle.get(key: String): String = this.getString(key)
