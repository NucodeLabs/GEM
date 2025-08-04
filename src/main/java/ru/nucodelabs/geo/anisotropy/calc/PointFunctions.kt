package ru.nucodelabs.geo.anisotropy.calc

import ru.nucodelabs.geo.anisotropy.Point

fun Point.zOfModelLayers(): List<Double> {
    val heightList: MutableList<Double> = mutableListOf()
    val power = model.map { it.power.value }
    var sum = z
    for (p in power) {
        sum -= p
        heightList.add(sum)
    }
    return heightList    // последняя уходит в бесконечность
}