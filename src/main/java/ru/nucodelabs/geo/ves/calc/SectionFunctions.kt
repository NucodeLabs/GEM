package ru.nucodelabs.geo.ves.calc

import ru.nucodelabs.geo.ves.SectionExperimentalDataSet

fun SectionExperimentalDataSet.xOfPicket(picketIndex: Int): Double {
    if (picketIndex == 0) return 0.0
    return pickets().subList(1, picketIndex + 1).map { it.offsetX }.reduce(Double::plus)
}

data class Bounds(val leftX: Double, val rightX: Double)

fun SectionExperimentalDataSet.picketsBounds(): List<Bounds> {
    val pickets = pickets()
    if (pickets.isEmpty()) {
        return emptyList()
    }

    val maxAb2 = pickets[0].sortedExperimentalData.maxOfOrNull { it.ab2 } ?: 0.0
    if (pickets.size == 1) {
        return listOf(Bounds(-maxAb2, +maxAb2))
    }

    val res = mutableListOf<Bounds>()
    var x = -pickets[1].offsetX / 2
    for (i in pickets.indices) {
        val leftX = x
        val rightX = if (i != pickets.lastIndex) {
            xOfPicket(i) + pickets[i + 1].offsetX / 2
        } else {
            xOfPicket(i) + (pickets[i - 1].offsetX / 2)
        }
        res += Bounds(leftX, rightX)
        x = rightX
    }

    return res
}

fun SectionExperimentalDataSet.length(): Double {
    val bounds = picketsBounds()
    return bounds.last().rightX - bounds.first().leftX
}