package ru.nucodelabs.algorithms.normalization

import ru.nucodelabs.data.ves.ExperimentalData
import ru.nucodelabs.gem.extensions.std.asFraction
import ru.nucodelabs.mathves.Normalization

data class FixableValue<T>(val value: T, val isFixed: Boolean)

fun normalizeExperimentalData(
    experimentalData: List<ExperimentalData>,
    distinctMn2: List<FixableValue<Double>>,
): List<Double> {
    return Normalization.signalNormalizationSchlumberger(
        distinctMn2.map { it.value }.toDoubleArray(),
        distinctMn2.map { it.isFixed }.toBooleanArray(),
        experimentalData.map { it.ab2 }.toDoubleArray(),
        experimentalData.map { data -> distinctMn2.indexOfFirst { it.value == data.mn2 } }.toIntArray(),
        experimentalData.map { it.resistanceApparent }.toDoubleArray(),
        experimentalData.map { it.errorResistanceApparent.asFraction() }.toDoubleArray()
    ).toList()
}