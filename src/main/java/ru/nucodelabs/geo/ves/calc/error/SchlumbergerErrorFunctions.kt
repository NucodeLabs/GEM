package ru.nucodelabs.geo.ves.calc.error

import ru.nucodelabs.mathves.SchlumbergerErrorFunctions
import ru.nucodelabs.util.std.asPercent
import ru.nucodelabs.util.std.fromPercent

data class MinMax(val min: Double, val max: Double)

fun measureError(value: Double, aError: Double, bError: Double): MinMax {
    val res = SchlumbergerErrorFunctions.calculateMeasureError(value, aError.fromPercent(), bError)
    return MinMax(res[0], res[1])
}

data class ValueMinMax(val value: Double, val min: Double, val max: Double)

infix fun MinMax.withValue(value: Double) = ValueMinMax(value, min, max)

fun kWithError(ab2: Double, mn2: Double, distAError: Double, distBError: Double): ValueMinMax {
    val res = SchlumbergerErrorFunctions.calculateKError(ab2, mn2, distAError.fromPercent(), distBError)
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
    isNew: Boolean,
    k: ValueMinMax,
    u: ValueMinMax,
    i: ValueMinMax
): ValueMinMaxAvgError {
    val res: DoubleArray
    if (isNew) {
        val muI = i.value
        val sigmaI = (i.max - i.min) / 6.0
        val cv = sigmaI / muI
        check(cv < 0.1) {
            "Коэффициент вариации > 0.1\nИспользуйте старую версию"
        }
        res = SchlumbergerErrorFunctions.approximateCalculateResistanceApparentError(
            k.value, k.min, k.max,
            u.value, u.min, u.max,
            i.value, i.min, i.max
        )
    } else {
        res = SchlumbergerErrorFunctions.calculateResistanceApparentError(
            k.value, k.min, k.max,
            u.value, u.min, u.max,
            i.value, i.min, i.max
        )
    }
    return ValueMinMaxAvgError(
        res[0],
        res[1],
        res[2],
        res[3],
        res[4].asPercent(),
    )
}
//    val k = _k.value
//    val k_min = _k.min
//    val k_max = _k.max
//    val U = _u.value
//    val U_min = _u.min
//    val U_max = _u.max
//    val I = _i.value
//    val I_min = _i.min
//    val I_max = _i.max
//
//    val sigma_U = (U_max - U_min) / 6.0;
//    val sigma_I = (I_max - I_min) / 6.0;
//    val Ro = U / I;
//    val variance_Ro = ((U * U) / (I * I)) * (((sigma_U * sigma_U) / (U * U)) + ((sigma_I * sigma_I) / (I * I)));
//
//    val variance_k = ((k_max - k_min) / 6.0) * ((k_max - k_min) / 6.0);
//    val Rok = k * Ro;
//    val Rok_avg = Rok;
//    val sigma_Rok = sqrt((variance_k + (k * k)) * (variance_Ro + (Ro * Ro)) - (Ro * Ro) * (k * k));
//    val Rok_min = Rok - 3.0 * sigma_Rok;
//    val Rok_max = Rok + 3.0 * sigma_Rok;
//    val Rok_err = (Rok_avg - Rok_min) / (Rok_avg);
//
//    return ValueMinMaxAvgError(Rok, Rok_min, Rok_max, Rok_avg, Rok_err)

fun resistanceApparentErrorForDistance(
    isNew: Boolean, k: ValueMinMax, u: Double, i: Double
): Double {
    return resistanceApparentWithError(
        isNew,
        k,
        measureError(u, 0.0, 0.0).withValue(u),
        measureError(i, 0.0, 0.0).withValue(i)
    ).error
}

fun resistanceApparentErrorForAmperage(
    isNew: Boolean, i: ValueMinMax, ab2: Double, mn2: Double, u: Double
): Double {
    return resistanceApparentWithError(
        isNew,
        kWithError(ab2, mn2, 0.0, 0.0),
        measureError(u, 0.0, 0.0).withValue(u),
        i
    ).error
}

fun resistanceApparentErrorForVoltage(
    isNew: Boolean, u: ValueMinMax, ab2: Double, mn2: Double, i: Double
): Double {
    return resistanceApparentWithError(
        isNew,
        kWithError(ab2, mn2, 0.0, 0.0),
        u,
        measureError(i, 0.0, 0.0).withValue(i)
    ).error
}