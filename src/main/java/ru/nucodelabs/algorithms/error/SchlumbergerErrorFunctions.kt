package ru.nucodelabs.algorithms.error

import ru.nucodelabs.gem.extensions.std.asFraction
import ru.nucodelabs.gem.extensions.std.asPercent
import ru.nucodelabs.mathves.SchlumbergerErrorFunctions

data class MinMax(val min: Double, val max: Double)

fun measureError(value: Double, aError: Double, bError: Double): MinMax {
    val res = SchlumbergerErrorFunctions.calculateMeasureError(value, aError.asFraction(), bError)
    return MinMax(res[0], res[1])
}

data class ValueMinMax(val value: Double, val min: Double, val max: Double)

infix fun MinMax.withValue(value: Double) = ValueMinMax(value, min, max)

fun kWithError(ab2: Double, mn2: Double, distAError: Double, distBError: Double): ValueMinMax {
    val res = SchlumbergerErrorFunctions.calculateKError(ab2, mn2, distAError.asFraction(), distBError)
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
        measureError(u, 0.0, 0.0).withValue(u),
        measureError(i, 0.0, 0.0).withValue(i)
    ).error
}

fun resistanceApparentErrorForAmperage(i: ValueMinMax, ab2: Double, mn2: Double, u: Double): Double {
    return resistanceApparentWithError(
        kWithError(ab2, mn2, 0.0, 0.0),
        measureError(u, 0.0, 0.0).withValue(u),
        i
    ).error
}

fun resistanceApparentErrorForVoltage(u: ValueMinMax, ab2: Double, mn2: Double, i: Double): Double {
    return resistanceApparentWithError(
        kWithError(ab2, mn2, 0.0, 0.0),
        u,
        measureError(i, 0.0, 0.0).withValue(i)
    ).error
}