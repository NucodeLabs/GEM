package ru.nucodelabs.gem.extensions.std

import java.text.DecimalFormat
import kotlin.math.pow

fun exp10(a: Double) = 10.0.pow(a)

fun exp10(a: Int) = 10.0.pow(a)

fun String.toNumber(decimalFormat: DecimalFormat) = decimalFormat.parse(this)
fun String?.toNumberOrNull(decimalFormat: DecimalFormat): Number? {
    if (this == null) {
        return null
    }
    return try {
        decimalFormat.parse(this)
    } catch (_: Exception) {
        null
    }
}