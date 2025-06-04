package ru.nucodelabs.geo.ves.calc.inverse

import org.apache.commons.math3.analysis.MultivariateFunction
import org.apache.commons.math3.optim.InitialGuess
import org.apache.commons.math3.optim.MaxEval
import org.apache.commons.math3.optim.PointValuePair
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer
import ru.nucodelabs.geo.ves.calc.forward.ForwardSolver
import ru.nucodelabs.geo.ves.calc.inverse.inverse_functions.FunctionValue
import ru.nucodelabs.geo.ves.calc.inverse.inverse_functions.SquaresDiff
import ru.nucodelabs.geo.ves.ExperimentalData
import ru.nucodelabs.geo.ves.ModelLayer
import ru.nucodelabs.geo.ves.Picket
import javax.inject.Inject

class InverseSolver @Inject constructor(private val forwardSolver: ForwardSolver) {
    private var picket: Picket? = null

    private val sideLength: Double = SIDE_LENGTH_DEFAULT
    private val relativeThreshold: Double = RELATIVE_THRESHOLD_DEFAULT
    private val absoluteThreshold: Double = ABSOLUTE_THRESHOLD_DEFAULT

    fun getOptimizedModelData(inputPicket: Picket): List<ModelLayer> {
        val MAX_EVAL = 100000
        this.picket = inputPicket
        val modelData = picket!!.modelData
        val modelResistance = modelData.filter { !it.isFixedResistance }.map { it.resistance }.toMutableList()
        val modelPower = modelData.filter { !it.isFixedPower }.map { it.power }.toMutableList()

        val minPower = picket!!.effectiveExperimentalData.minOf { it.ab2 }
        val maxPower = picket!!.effectiveExperimentalData.maxOf { it.ab2 }

        setLimitValues(modelResistance, 0.1, 1e5.0, modelPower, 0.1, maxPower)

        val fixedModelResistance = modelData.filter { it.isFixedResistance }.map { it.resistance }
        val fixedModelPower = modelData.filter { it.isFixedPower }.map { it.power }

        val optimizer = SimplexOptimizer(relativeThreshold, absoluteThreshold)
        val multivariateFunction: MultivariateFunction = FunctionValue(picket!!.effectiveExperimentalData, SquaresDiff(), modelData, forwardSolver)

        val dimension = modelResistance.size + modelPower.size - 1
        val nelderMeadSimplex = NelderMeadSimplex(dimension, sideLength)
        val startPoint = DoubleArray(dimension)
        for (i in modelResistance.indices) {
            startPoint[i] = kotlin.math.log(modelResistance[i])
        }
        for (i in modelResistance.size until dimension) {
            startPoint[i] = kotlin.math.log(modelPower[i - modelResistance.size])
        }

        val initialGuess = InitialGuess(startPoint)
        val pointValuePair: PointValuePair = optimizer.optimize(
            MaxEval(MAX_EVAL),
            ObjectiveFunction(multivariateFunction),
            GoalType.MINIMIZE,
            initialGuess,
            nelderMeadSimplex
        )

        val key = pointValuePair.key

        val newModelPower = mutableListOf<Double>()
        val newModelResistance = mutableListOf<Double>()

        var cntFixedResistances = 0
        var cntUnfixedResistances = 0
        for (modelLayer in modelData) {
            if (modelLayer.isFixedResistance) {
                newModelResistance.add(fixedModelResistance[cntFixedResistances])
                cntFixedResistances++
            } else {
                newModelResistance.add(kotlin.math.exp(key[cntUnfixedResistances]))
                cntUnfixedResistances++
            }
        }

        var cntFixedPowers = 0
        var cntUnfixedPowers = 0
        for (i in 0 until modelData.size - 1) {
            val modelLayer = modelData[i]
            val shift = modelResistance.size
            if (modelLayer.isFixedPower) {
                newModelPower.add(fixedModelPower[cntFixedPowers])
                cntFixedPowers++
            } else {
                newModelPower.add(kotlin.math.exp(key[cntUnfixedPowers + shift]))
                cntUnfixedPowers++
            }
        }
        newModelPower.add(0.0)

        val resultModel = mutableListOf<ModelLayer>()
        for (i in modelData.indices) {
            resultModel.add(
                ModelLayer(
                    newModelPower[i],
                    newModelResistance[i],
                    modelData[i].isFixedPower,
                    modelData[i].isFixedResistance
                )
            )
        }
        return resultModel
    }

    private fun setLimitValues(
        resistances: MutableList<Double>, minResistance: Double, maxResistance: Double,
        powers: MutableList<Double>, minPower: Double, maxPower: Double
    ) {
        for (i in resistances.indices) {
            when {
                resistances[i] < minResistance -> resistances[i] = minResistance
                resistances[i] > maxResistance -> resistances[i] = maxResistance
            }
        }
        for (i in powers.indices) {
            when {
                powers[i] != 0.0 && powers[i] < minPower -> powers[i] = minPower
                powers[i] > maxPower -> powers[i] = maxPower
            }
        }
    }

    companion object {
        private const val SIDE_LENGTH_DEFAULT = 1.0
        private const val RELATIVE_THRESHOLD_DEFAULT = 1e-10
        private const val ABSOLUTE_THRESHOLD_DEFAULT = 1e-30
    }
}
