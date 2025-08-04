package ru.nucodelabs.gem.fxmodel.ves.app

import jakarta.inject.Inject
import ru.nucodelabs.geo.forward.ForwardSolver
import ru.nucodelabs.geo.target.RelativeErrorAwareTargetFunction
import ru.nucodelabs.geo.ves.ExperimentalData
import ru.nucodelabs.geo.ves.ModelLayer
import ru.nucodelabs.geo.ves.calc.initialModel.SimpleInitialModel.threeLayersInitialModel
import ru.nucodelabs.geo.ves.calc.initialModel.multiLayerInitialModel
import ru.nucodelabs.geo.ves.calc.inverse.InverseSolver

class InitialModelService @Inject constructor(
    private val forwardSolver: ForwardSolver,
    private val inverseSolver: InverseSolver,
    private val targetFunction: RelativeErrorAwareTargetFunction,
) {
    fun arbitraryInitialModel(
        signals: List<ExperimentalData>,
        minTargetFunctionValue: Double,
        maxLayersCount: Int,
    ): List<ModelLayer> {
        return multiLayerInitialModel(
            signals = signals,
            forwardSolver = forwardSolver,
            inverseSolver = inverseSolver,
            targetFunction = targetFunction,
            minTargetFunctionValue = minTargetFunctionValue,
            maxLayersCount = maxLayersCount
        )
    }

    fun simpleInitialModel(signals: List<ExperimentalData>): List<ModelLayer> {
        return threeLayersInitialModel(signals)
    }
}