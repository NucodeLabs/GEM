package ru.nucodelabs.geo.ves.calc

import ru.nucodelabs.geo.ves.Picket

fun Picket.zOfModelLayers(): List<Double> {
    val heightList: MutableList<Double> = mutableListOf()
    val power = modelData.map { it.power }
    var sum = z
    for (p in power) {
        sum -= p
        heightList.add(sum)
    }
    return heightList    // последняя уходит в бесконечность
}

/**
 * Пусть `a` - массив, возвращенный методом.
 *
 * Тогда верно следующее:
 *
 * `a[i] = j` `<=>` `effectiveExperimentalData[i] = sortedExperimentalData[j]`
 */
fun Picket.effectiveToSortedIndicesMapping(): IntArray {
    return IntArray(effectiveExperimentalData.size) { i ->
        sortedExperimentalData.indexOf(effectiveExperimentalData[i])
    }
}