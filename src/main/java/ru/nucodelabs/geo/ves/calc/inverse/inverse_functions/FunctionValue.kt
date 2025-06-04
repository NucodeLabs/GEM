package ru.nucodelabs.geo.ves.calc.inverse.inverse_functions

import org.apache.commons.math3.analysis.MultivariateFunction
import ru.nucodelabs.geo.ves.calc.forward.ForwardSolver
import ru.nucodelabs.geo.ves.ExperimentalData
import ru.nucodelabs.geo.ves.ModelLayer

class FunctionValue(
    private val experimentalData: List<ExperimentalData>,
    private val inverseFunction: (List<Double>, List<Double>) -> Double,
    private val modelLayers: List<ModelLayer>,
    private val forwardSolver: ForwardSolver
) : MultivariateFunction {

    private var diffMinValue = Double.MAX_VALUE

    override fun value(variables: DoubleArray): Double {
        val unfixedResistancesCnt = modelLayers.count { !it.isFixedResistance }

        val currentModelResistance = variables
            .take(unfixedResistancesCnt)
            .map { kotlin.math.exp(it) }

        val currentModelPower = variables
            .drop(unfixedResistancesCnt)
            .map { kotlin.math.exp(it) }
            .toMutableList()
            .apply { add(0.0) }

        var resIdx = 0
        val newModelResistance = modelLayers.map { layer ->
            if (layer.isFixedResistance) layer.resistance else currentModelResistance[resIdx++]
        }

        var powIdx = 0
        val newModelPower = modelLayers.map { layer ->
            if (layer.isFixedPower) layer.power else currentModelPower[powIdx++]
        }

        val newModelLayers = newModelPower.zip(newModelResistance) { p, r ->
            ModelLayer(p, r, false, false)
        }

        val solvedResistance = forwardSolver.invoke(experimentalData, newModelLayers)

        var diffValue = inverseFunction(
            solvedResistance,
            experimentalData.map { it.resistanceApparent }
        )

        val invalid = newModelLayers.any { layer ->
            layer.resistance < 0.1 ||
                layer.resistance > 1e5 ||
                (layer.power != 0.0 && layer.power < 0.1) ||
                layer.power > experimentalData.last().ab2
        }

        diffValue = if (invalid) {
            kotlin.math.max(diffMinValue * (1.1 + 0.1 * Math.random()), diffValue)
        } else {
            diffMinValue = kotlin.math.min(diffValue, diffMinValue)
            diffValue
        }
        return diffValue
    }
}
