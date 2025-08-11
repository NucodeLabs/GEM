package ru.nucodelabs.util.std

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}

fun MutableList<*>.removeAllAt(indices: Collection<Int>) {
    for (index in indices.sorted().reversed()) {
        this.removeAt(index)
    }
}