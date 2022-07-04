package ru.nucodelabs.gem.view.tables

fun MutableList<*>.removeAllAt(indices: Collection<Int>) {
    for (index in indices.sorted().reversed()) {
        this.removeAt(index)
    }
}