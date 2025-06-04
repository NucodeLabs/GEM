package ru.nucodelabs.gem.view

import javafx.util.StringConverter
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Formats numbers as powers of ten when the value is integer.
 */
class PowerOf10Formatter : StringConverter<Number>() {

    private fun toUpperIndex(value: String): String = buildString {
        value.forEach { c ->
            append(
                when (c) {
                    '1' -> '¹'
                    '2' -> '²'
                    '3' -> '³'
                    '4' -> '⁴'
                    '5' -> '⁵'
                    '6' -> '⁶'
                    '7' -> '⁷'
                    '8' -> '⁸'
                    '9' -> '⁹'
                    '0' -> '⁰'
                    '.' -> '\u0387'
                    '-' -> '\u207b'
                    else -> c
                }
            )
        }
    }

    override fun toString(number: Number): String {
        val format = DecimalFormat().apply {
            decimalFormatSymbols = DecimalFormatSymbols(Locale.US).apply {
                decimalSeparator = '.'
            }
        }
        return when {
            number.toDouble() == 0.0 -> "1"
            number.toDouble() - Math.ceil(number.toDouble()) == 0.0 ->
                "10" + toUpperIndex(format.format(number.toDouble()))
            else -> ""
        }
    }

    override fun fromString(string: String?): Number? {
        throw UnsupportedOperationException()
    }
}
