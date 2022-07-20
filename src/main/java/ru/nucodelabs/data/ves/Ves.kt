package ru.nucodelabs.data.ves

import kotlin.math.PI

fun rhoA(ab2: Double, mn2: Double, amperage: Double, voltage: Double) =
    k(ab2, mn2) * (voltage / amperage)

fun k(ab2: Double, mn2: Double): Double {
    val am = ab2 - mn2
    val bm = ab2 + mn2
    return k(am = am, bm = bm, an = bm, bn = am)
}

fun k(am: Double, bm: Double, an: Double, bn: Double) = (2 * PI) / ((1 / am) - (1 / bm) - (1 / an) + (1 / bn))

fun u(rhoA: Double, amperage: Double, k: Double) = (rhoA * amperage) / k