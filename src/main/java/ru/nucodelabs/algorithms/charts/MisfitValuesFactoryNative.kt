package ru.nucodelabs.algorithms.charts

import ru.nucodelabs.algorithms.forward_solver.ForwardSolver
import ru.nucodelabs.data.ves.ExperimentalData
import ru.nucodelabs.data.ves.ModelLayer
import kotlin.math.abs
import kotlin.math.sign

internal class MisfitValuesFactoryNative(val forwardSolver: ForwardSolver) : MisfitsFunction {

    override fun invoke(experimentalData: List<ExperimentalData>, modelData: List<ModelLayer>): List<Double> {
        if (experimentalData.isEmpty() || modelData.isEmpty()) {
            return listOf()
        }

        val resistanceApparent = experimentalData.map { it.resistanceApparent }
        val errorResistanceApparent = experimentalData.map { it.errorResistanceApparent }

        val solvedResistance = forwardSolver(experimentalData, modelData)
        val res = mutableListOf<Double>()
        for (i in experimentalData.indices) {
            val value = abs(
                MisfitFunctions.calculateRelativeDeviationWithError(
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