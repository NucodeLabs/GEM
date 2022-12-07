package ru.nucodelabs.gem.util.std

import java.text.DecimalFormat
import kotlin.math.pow

fun exp10(a: Double) = 10.0.pow(a)

fun exp10(a: Int) = 10.0.pow(a)

fun String.toNumber(decimalFormat: DecimalFormat) = decimalFormat.parse(this)
fun String.toNumberOrNull(decimalFormat: DecimalFormat): Number? {
    return try {
        decimalFormat.parse(this)
    } catch (_: Exception) {
        null
    }
}

fun String.toDoubleOrNullBy(decimalFormat: DecimalFormat) = toNumberOrNull(decimalFormat)?.toDouble()

/**
 * e.g. 0.5 -> 50 (%)
 */
fun Double.asPercent(): Double = this * 100.0

/**
 * e.g. 50 (%) -> 0.5
 */
fun Double.asFraction(): Double = this / 100.0

const val MILLI = 1e-3
fun asMilli(value: Double) = value / MILLI

fun fromMilli(value: Double) = value * MILLI