package ru.nucodelabs.gem.fxmodel.ves.app

import ru.nucodelabs.geo.target.TargetFunction
import ru.nucodelabs.geo.ves.ExperimentalData
import ru.nucodelabs.geo.ves.ModelLayer
import ru.nucodelabs.geo.ves.calc.forward.ForwardSolver
import ru.nucodelabs.geo.ves.calc.initialModel.SimpleInitialModel.threeLayersInitialModel
import ru.nucodelabs.geo.ves.calc.initialModel.multiLayerInitialModel
import ru.nucodelabs.geo.ves.calc.inverse.InverseSolver
import javax.inject.Inject

class InitialModelService @Inject constructor(
    private val forwardSolver: ForwardSolver,
    private val inverseSolver: InverseSolver,
    private val targetFunction: TargetFunction.WithError,
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