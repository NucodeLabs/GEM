package ru.nucodelabs.gem.utils

import javafx.beans.binding.Bindings
import javafx.collections.ObservableList

fun ObservableList<*>.emptyBinding() = Bindings.createBooleanBinding({ isEmpty() }, this)
