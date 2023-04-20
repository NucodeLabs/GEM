package ru.nucodelabs.geo.ves.calc.initialModel

import ru.nucodelabs.geo.target.SquareDiffTargetFunction
import ru.nucodelabs.geo.target.TargetFunction
import ru.nucodelabs.geo.target.invoke
import ru.nucodelabs.geo.ves.ExperimentalData
import ru.nucodelabs.geo.ves.ModelLayer
import ru.nucodelabs.geo.ves.calc.divide
import ru.nucodelabs.geo.ves.calc.forward.ForwardSolver
import ru.nucodelabs.geo.ves.calc.inverse.InverseSolver
import ru.nucodelabs.geo.ves.calc.inverse.invoke
import kotlin.math.pow

const val MAX_LAYERS_COUNT = 10
const val MIN_TARGET_FUN_VALUE = 0.1
const val MAX_EVAL_INVERSE = 10_000_000

private fun initialModel(signals: List<ExperimentalData>): List<ModelLayer> =
    SimpleInitialModel.threeLayersInitialModel(signals)

private fun fines(count: Int): List<Double> = List(count) { x -> 1.2.pow(x) }

fun multiLayerInitialModel(
    signals: List<ExperimentalData>,
    initialModel: List<ModelLayer> = initialModel(signals),
    forwardSolver: ForwardSolver = ForwardSolver(),
    targetFunction: TargetFunction.WithError = SquareDiffTargetFunction(),
    inverseSolver: InverseSolver = InverseSolver(forwardSolver, targetFunction),
    maxLayersCount: Int = MAX_LAYERS_COUNT,
    minTargetFunctionValue: Double = MIN_TARGET_FUN_VALUE,
    maxEval: Int = MAX_EVAL_INVERSE,
    iterationCallback: (
        model: List<ModelLayer>,
        targetFunctionValue: Double,
    ) -> Unit = { _, _ -> }
): List<ModelLayer> {
    var model = initialModel
    val initialLayersCount = model.size

    check(initialLayersCount in 1 until maxLayersCount)

    for (i in initialLayersCount..maxLayersCount) {
        val targetFunctionValue = targetFunction(
            forwardSolver(signals, model),
            signals.map { it.resistanceApparent },
            signals.map { it.errorResistanceApparent }
        )

        iterationCallback(model, targetFunctionValue)

        if (targetFunctionValue <= minTargetFunctionValue) {
            break
        }

        val maxLayer = model.maxBy { it.power }
        val zeroPowerLayer = model.last()
        model = model - maxLayer - zeroPowerLayer + maxLayer.divide().toList() + zeroPowerLayer
        model = inverseSolver(signals, model, maxEval)
    }

    return model
}