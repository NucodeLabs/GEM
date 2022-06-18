package ru.nucodelabs.data.ves

import kotlin.math.PI

internal fun zOfPower(modelData: List<ModelLayer>, picketZ: Double): List<Double> {
    val heightList: MutableList<Double> = mutableListOf()
    val power = modelData.map { it.power }
    var sum = picketZ

    for (p in power) {
        sum -= p
        heightList.add(sum)
    }

    // последняя уходит в бесконечность
    return heightList
}

internal fun xOfPicket(section: Section, index: Int): Double {
    var x = 0.0
    for (i in 0..index) {
        x += section.pickets[i].offsetX
    }
    return x
}


internal fun resistanceApparent(ab2: Double, mn2: Double, amperage: Double, voltage: Double) =
    k(ab2, mn2) * (voltage / amperage)

private fun k(ab2: Double, mn2: Double): Double {
    val am = ab2 - mn2
    val bm = ab2 + mn2
    return (2 * PI
            / (1 / am - 1 / bm - 1 / bm
            + 1 / am))
}
