package ru.nucodelabs.geo.ves.calc

import ru.nucodelabs.gem.util.std.asFraction
import ru.nucodelabs.geo.ves.ExperimentalData
import ru.nucodelabs.mathves.Normalization

data class FixableValue<T>(val value: T, val isFixed: Boolean)

/**
 * @return normalized resistance and additive coefficients
 */
fun normalizeExperimentalData(
    experimentalData: List<ExperimentalData>,
    distinctMn2: List<FixableValue<Double>>,
    idxMap: List<Int>
): Pair<List<Double>, List<Double>> {
    val add = DoubleArray(distinctMn2.size)
    return Normalization.signalNormalizationSchlumberger(
        distinctMn2.map { it.value }.toDoubleArray(),
        distinctMn2.map { it.isFixed }.toBooleanArray(),
        experimentalData.map { it.ab2 }.toDoubleArray(),
        idxMap.map { it.toShort() }.toShortArray(),
        experimentalData.map { it.resistanceApparent }.toDoubleArray(),
        experimentalData.map { it.errorResistanceApparent.asFraction() }.toDoubleArray(),
        add
    ).toList() to add.toList()
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