package ru.nucodelabs.data.ves

import kotlin.math.PI

internal fun rhoA(ab2: Double, mn2: Double, amperage: Double, voltage: Double) =
    k(ab2, mn2) * (voltage / amperage)

private fun k(ab2: Double, mn2: Double): Double {
    val am = ab2 - mn2
    val bm = ab2 + mn2
    return (2 * PI / (1 / am - 1 / bm - 1 / bm + 1 / am))
}