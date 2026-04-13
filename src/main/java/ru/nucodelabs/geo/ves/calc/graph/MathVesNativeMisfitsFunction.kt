package ru.nucodelabs.geo.ves.calc.graph

import ru.nucodelabs.geo.forward.ForwardSolver
import ru.nucodelabs.geo.ves.ReadOnlyExperimentalSignal
import ru.nucodelabs.geo.ves.ReadOnlyModelLayer
import ru.nucodelabs.geo.ves.calc.adapter.invoke
import kotlin.math.abs
import kotlin.math.sign

internal class MathVesNativeMisfitsFunction(val forwardSolver: ForwardSolver) : MisfitsFunction {

    override fun invoke(
        experimentalData: List<ReadOnlyExperimentalSignal>,
        modelData: List<ReadOnlyModelLayer>
    ): List<Double> {
        if (experimentalData.isEmpty() || modelData.isEmpty()) {
            return listOf()
        }

        val resistivityApparent = experimentalData.map { it.resistivityApparent }
        val errorResistivityApparent = experimentalData.map { it.errorResistivityApparent }

        val solvedResistivity = forwardSolver(experimentalData, modelData)
        val res = ArrayList<Double>(experimentalData.size)
        for (i in experimentalData.indices) {
            val value = abs(
                ru.nucodelabs.mathves.MisfitFunctions.calculateRelativeDeviationWithError(
                    resistivityApparent[i],
                    errorResistivityApparent[i] / 100f,
                    solvedResistivity[i]
                )
            ) * sign(solvedResistivity[i] - resistivityApparent[i]) * 100f

            res.add(value)
        }
        return res
    }
}