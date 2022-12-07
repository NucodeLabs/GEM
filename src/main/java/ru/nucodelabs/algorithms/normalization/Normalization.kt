package ru.nucodelabs.algorithms.normalization

import ru.nucodelabs.data.ves.ExperimentalData
import ru.nucodelabs.gem.extensions.std.asFraction
import ru.nucodelabs.mathves.Normalization

data class FixableValue<T>(val value: T, val isFixed: Boolean)

fun normalizeExperimentalData(
    experimentalData: List<ExperimentalData>,
    distinctMn2: List<FixableValue<Double>>,
    idxMap: List<Int>
): List<Double> {
    return Normalization.signalNormalizationSchlumberger(
        distinctMn2.map { it.value }.toDoubleArray(),
        distinctMn2.map { it.isFixed }.toBooleanArray(),
        experimentalData.map { it.ab2 }.toDoubleArray(),
        idxMap.map { it.toShort() }.toShortArray(),
        experimentalData.map { it.resistanceApparent }.toDoubleArray(),
        experimentalData.map { it.errorResistanceApparent.asFraction() }.toDoubleArray()
    ).toList()
}

@JvmName("distinctMn2ForExpData")
fun distinctMn2(experimentalData: List<ExperimentalData>) = distinctMn2(experimentalData.map { it.mn2 })

fun distinctMn2(mn2: List<Double>): Pair<List<Double>, List<Int>> {
    val idx = ShortArray(mn2.size)
    val distinct = Normalization.distinctMn2(
        mn2.toDoubleArray(),
        idx
    )
    return distinct.toList() to idx.toList().map { it.toInt() }
}