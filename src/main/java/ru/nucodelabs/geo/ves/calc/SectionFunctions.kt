package ru.nucodelabs.geo.ves.calc

import ru.nucodelabs.geo.ves.Picket
import ru.nucodelabs.geo.ves.Section

fun Section.xOfPicket(picket: Picket): Double = xOfPicket(pickets.indexOf(picket))

fun Section.xOfPicket(index: Int): Double {
    require(index >= 0)
    if (index == 0) return 0.0

    var x = 0.0
    for (i in 1..index) {
        x += this.pickets[i].offsetX
    }
    return x
}

data class Bounds(val leftX: Double, val rightX: Double)

fun Section.picketsBounds(): List<Bounds> {
    if (pickets.isEmpty()) {
        return emptyList()
    }

    val maxAb2 = pickets[0].sortedExperimentalData.maxOfOrNull { it.ab2 } ?: 0.0
    if (pickets.size == 1) {
        return listOf(Bounds(-maxAb2, +maxAb2))
    }

    val res = mutableListOf<Bounds>()
    var x = -maxAb2
    for (i in pickets.indices) {
        val leftX = x
        val rightX = if (i != pickets.lastIndex) {
            xOfPicket(i) + pickets[i + 1].offsetX / 2
        } else {
            xOfPicket(i) + (pickets.last().sortedExperimentalData.maxOfOrNull { it.ab2 } ?: 0.0)
        }
        res += Bounds(leftX, rightX)
        x = rightX
    }

    return res
}

fun Section.length(): Double {
    val bounds = picketsBounds()
    return bounds.last().rightX - bounds.first().leftX
}