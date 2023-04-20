package ru.nucodelabs.geo.target

import kotlin.math.pow

class SquareDiffTargetFunction : TargetFunction.WithError {
    override fun apply(theoretical: List<Double>, experimental: List<Double>, relativeError: List<Double>): Double {
        check(theoretical.size == experimental.size && experimental.size == relativeError.size) {
            "Lists sizes must be equal"
        }

        var functionValue = 0.0
        for (i in theoretical.indices) {
            functionValue += (
                    (experimental[i] - theoretical[i]).pow(2.0)
//                            * ((experimental[i] - theoretical[i]) / (relativeError[i] * experimental[i]))
                    )
//            умножить полевой сигнал на относительную погрешность (rho_e - rho_t) / d_ohm; d_ohm = rho_e * d
        }
        return StrictMath.sqrt(functionValue) / theoretical.size
    }

}