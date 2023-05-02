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
import kotlin.math.log2
import kotlin.math.pow

const val MAX_LAYERS_COUNT = 10
const val MAX_EVAL_INVERSE = 10_000_000
const val MIN_TARGET_FUNCTION_VALUE = 1.0

private fun initialModel(signals: List<ExperimentalData>): List<ModelLayer> =
        listOf(
                ModelLayer(
                        power = 0.0,
                        resistance = 2.0.pow(signals.map { log2(it.ab2) }.average())
                )
        )

// сопр среднее по логарифму, потом обратно в степень, с 1 слоем

fun multiLayerInitialModel(
        signals: List<ExperimentalData>,
        initialModel: List<ModelLayer> = initialModel(signals),
        forwardSolver: ForwardSolver = ForwardSolver(),
        targetFunction: TargetFunction.WithError = SquareDiffTargetFunction(),
        inverseSolver: InverseSolver = InverseSolver(forwardSolver, targetFunction),
        maxLayersCount: Int = MAX_LAYERS_COUNT,
        maxEval: Int = MAX_EVAL_INVERSE,
        minTargetFunctionValue: Double = MIN_TARGET_FUNCTION_VALUE,
        breakAfterFoundResult: Boolean = true,
        iterationInterceptor: (
                model: List<ModelLayer>,
                targetFunctionValue: Double,
                isResult: Boolean,
        ) -> Unit = { _, _, _ -> },
): List<ModelLayer> {
    var model = initialModel
    val initialLayersCount = model.size
    var result: List<ModelLayer>? = null

    check(initialLayersCount in 1 until maxLayersCount)

    while (model.size <= maxLayersCount) {
        model = inverseSolver(signals, model, maxEval)

        val targetFunctionValue = targetFunction(
            forwardSolver(signals, model),
            signals.map { it.resistanceApparent },
            signals.map { it.errorResistanceApparent }
        )

        val isResult = targetFunctionValue <= minTargetFunctionValue && result == null

        iterationInterceptor(model, targetFunctionValue, isResult)

        if (isResult) {
            result = model
            if (breakAfterFoundResult) {
                break
            }
        }

        if (model.size == maxLayersCount) {
            break
        }

        model = if (model.size >= 2) {
            val maxLayer = model.maxBy { it.power }
            val zeroPowerLayer = model.last()
            model - maxLayer - zeroPowerLayer + maxLayer.divide().toList() + zeroPowerLayer
        } else {
            listOf(model.first().copy(power = signals.maxOf { it.ab2 } / 4), model.first())
        }
    }

    return model
}