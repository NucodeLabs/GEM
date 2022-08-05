package ru.nucodelabs.algorithms.normalization

import ru.nucodelabs.data.ves.ExperimentalData
import ru.nucodelabs.mathves.Normalization

data class FixableValue<T>(val value: T, val fixed: Boolean)

fun normalizeExperimentalData(
    experimentalData: List<ExperimentalData>,
    distinctMn2: List<FixableValue<Double>>,
    idxMap: Map<ExperimentalData, Int>,
): List<Double> {
    return Normalization.signalNormalizationSchlumberger(
        distinctMn2.map { it.value }.toDoubleArray(),
        distinctMn2.map { it.fixed }.toBooleanArray(),
        experimentalData.map { it.ab2 }.toDoubleArray(),
        experimentalData.map { idxMap[it] ?: throw IllegalArgumentException() }.toIntArray(),
        experimentalData.map { it.resistanceApparent }.toDoubleArray(),
        experimentalData.map { it.errorResistanceApparent }.toDoubleArray()
    ).toList()
}