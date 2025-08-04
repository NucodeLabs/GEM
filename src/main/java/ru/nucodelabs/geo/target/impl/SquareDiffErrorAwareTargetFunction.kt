package ru.nucodelabs.geo.target.impl

import ru.nucodelabs.gem.util.std.fromPercent
import ru.nucodelabs.geo.target.RelativeErrorAwareTargetFunction
import kotlin.math.pow

/**
 *
 */
class SquareDiffTargetFunction : RelativeErrorAwareTargetFunction {
    override fun invoke(theoretical: List<Double>, experimental: List<Double>, relativeError: List<Double>): Double {
        check(theoretical.size == experimental.size && experimental.size == relativeError.size) {
            "Lists sizes must be equal"
        }

        var functionValue = 0.0
        for (i in theoretical.indices) {
            functionValue += (
                    ((experimental[i] - theoretical[i])
                            / (relativeError[i].fromPercent() * experimental[i])).pow(2.0))
//            умножить полевой сигнал на относительную погрешность (rho_e - rho_t) / d_ohm; d_ohm = rho_e * d
        }
        return StrictMath.sqrt(functionValue / theoretical.size)
    }

}