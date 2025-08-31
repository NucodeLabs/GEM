package ru.nucodelabs.geo.ves.calc.graph

import ru.nucodelabs.geo.forward.ForwardSolver
import ru.nucodelabs.geo.ves.ExperimentalData
import ru.nucodelabs.geo.ves.ModelLayer
import ru.nucodelabs.geo.ves.calc.adapter.invoke
import kotlin.math.abs
import kotlin.math.sign

internal class MathVesNativeMisfitsFunction(val forwardSolver: ForwardSolver) : MisfitsFunction {

    override fun invoke(experimentalData: List<ExperimentalData>, modelData: List<ModelLayer>): List<Double> {
        if (experimentalData.isEmpty() || modelData.isEmpty()) {
            return listOf()
        }

        val resistanceApparent = experimentalData.map { it.resistanceApparent }
        val errorResistanceApparent = experimentalData.map { it.errorResistanceApparent }

        val solvedResistance = forwardSolver(experimentalData, modelData)
        val res = ArrayList<Double>(experimentalData.size)
        for (i in experimentalData.indices) {
            val value = abs(
                ru.nucodelabs.mathves.MisfitFunctions.calculateRelativeDeviationWithError(
                    resistanceApparent[i],
                    errorResistanceApparent[i] / 100f,
                    solvedResistance[i]
                )
            ) * sign(solvedResistance[i] - resistanceApparent[i]) * 100f

            res.add(value)
        }
        return res
    }
}