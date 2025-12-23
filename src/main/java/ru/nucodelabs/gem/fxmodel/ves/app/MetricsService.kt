package ru.nucodelabs.gem.fxmodel.ves.app

import jakarta.inject.Inject
import ru.nucodelabs.geo.forward.ForwardSolver
import ru.nucodelabs.geo.target.RelativeErrorAwareTargetFunction
import ru.nucodelabs.geo.ves.ReadOnlyExperimentalSignal
import ru.nucodelabs.geo.ves.ReadOnlyModelLayer
import ru.nucodelabs.geo.ves.calc.adapter.invoke
import ru.nucodelabs.geo.ves.calc.graph.MisfitsFunction
import kotlin.math.abs

class MetricsService @Inject constructor(
    private val forwardSolver: ForwardSolver,
    private val targetFunction: RelativeErrorAwareTargetFunction,
    private val misfitsFunction: MisfitsFunction
) {

    fun targetFunctionValue(signals: List<ReadOnlyExperimentalSignal>, model: List<ReadOnlyModelLayer>): Double {
        return targetFunction(
            forwardSolver(signals, model),
            signals.map { it.resistivityApparent },
            signals.map { it.errorResistivityApparent }
        )
    }

    fun errorValue(signals: List<ReadOnlyExperimentalSignal>, model: List<ReadOnlyModelLayer>): List<Double> {
        return misfitsFunction(signals, model)
    }

    fun errorAvgMax(signals: List<ReadOnlyExperimentalSignal>, model: List<ReadOnlyModelLayer>): AvgMax {
        val error = errorValue(signals, model)
        val avg = error.map { abs(it) }.average()
        val max = error.maxOfOrNull { abs(it) } ?: 0.0
        return AvgMax(avg, max)
    }

    fun misfitsValue(signals: List<ReadOnlyExperimentalSignal>, model: List<ReadOnlyModelLayer>): List<Double> {
        val theor = forwardSolver(signals, model)
        val exp = signals.map { it.resistivityApparent }
        return exp.mapIndexed { idx, resApp -> ((resApp - theor[idx]) / resApp) * 100 }
    }

    fun misfitsAvgMax(signals: List<ReadOnlyExperimentalSignal>, model: List<ReadOnlyModelLayer>): AvgMax {
        val misfits = misfitsValue(signals, model)
        val avg = misfits.map { abs(it) }.average()
        val max = misfits.maxOfOrNull { abs(it) } ?: 0.0
        return AvgMax(avg, max)
    }

}

data class AvgMax(val avg: Double, val max: Double)
