package ru.nucodelabs.algorithms.error

import ru.nucodelabs.mathves.SchlumbergerErrorFunctions

/**
 * e.g. 0.5 -> 50 (%)
 */
fun Double.asPercent(): Double = this * 100.0

/**
 * e.g. 50 (%) -> 0.5
 */
fun Double.asFraction(): Double = this / 100.0
data class MinMax(val min: Double, val max: Double)

fun measureError(value: Double, aError: Double, bError: Double): MinMax {
    val res = SchlumbergerErrorFunctions.calculateMeasureError(value, aError.asFraction(), bError.asFraction())
    return MinMax(res[0], res[1])
}

data class ValueMinMax(val value: Double, val min: Double, val max: Double)

fun MinMax.withValue(value: Double) = ValueMinMax(value, min, max)

fun kWithError(ab2: Double, mn2: Double, distAError: Double, distBError: Double): ValueMinMax {
    val res = SchlumbergerErrorFunctions.calculateKError(ab2, mn2, distAError.asFraction(), distBError.asFraction())
    return ValueMinMax(res[0], res[1], res[2])
}

data class ValueMinMaxAvgError(
    val value: Double,
    val min: Double,
    val max: Double,
    val avg: Double,
    val error: Double,
)

fun resistanceApparentWithError(
    k: ValueMinMax,
    u: ValueMinMax,
    i: ValueMinMax
): ValueMinMaxAvgError {
    val res = SchlumbergerErrorFunctions.calculateResistanceApparentError(
        k.value, k.min, k.max,
        u.value, u.min, u.max,
        i.value, i.min, i.max
    )

    return ValueMinMaxAvgError(
        res[0],
        res[1],
        res[2],
        res[3],
        res[4].asPercent(),
    )
}

fun resistanceApparentErrorForDistance(k: ValueMinMax, u: Double, i: Double): Double {
    return resistanceApparentWithError(
        k,
        ValueMinMax(u, u, u),
        ValueMinMax(i, i, i)
    ).error
}

fun resistanceApparentErrorForAmperage(i: ValueMinMax, k: Double, u: Double): Double {
    return resistanceApparentWithError(
        ValueMinMax(k, k, k),
        ValueMinMax(u, u, u),
        i
    ).error
}

fun resistanceApparentErrorForVoltage(u: ValueMinMax, k: Double, i: Double): Double {
    return resistanceApparentWithError(
        ValueMinMax(k, k, k),
        u,
        ValueMinMax(i, i, i)
    ).error
}