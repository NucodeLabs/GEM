package ru.nucodelabs.geo.ves.calc

import ru.nucodelabs.geo.ves.ExperimentalDataSet
import ru.nucodelabs.geo.ves.ModelDataSet

fun ModelDataSet.zOfModelLayers(): List<Double> {
    val heightList: MutableList<Double> = mutableListOf()
    val power = modelData.map { it.power }
    var sum = modelZ
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
 * `a[i] = j <=> effectiveExperimentalData[i] = sortedExperimentalData[j]`
 */
fun ExperimentalDataSet.effectiveToSortedIndicesMapping(): IntArray {
    val sorted = sortedExperimentalData
    val effective = effectiveExperimentalData
    return IntArray(effective.size) { i -> sorted.indexOf(effective[i]) }
}