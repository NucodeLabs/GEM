package ru.nucodelabs.gem.fxmodel.ves.app

import ru.nucodelabs.geo.forward.ForwardSolver
import ru.nucodelabs.geo.target.TargetFunction
import ru.nucodelabs.geo.ves.ExperimentalData
import ru.nucodelabs.geo.ves.ModelLayer
import ru.nucodelabs.geo.ves.calc.adapter.invoke
import ru.nucodelabs.geo.ves.calc.graph.MisfitsFunction
import javax.inject.Inject
import kotlin.math.abs

class MetricsService @Inject constructor(
    private val forwardSolver: ForwardSolver,
    private val targetFunction: TargetFunction.WithError,
    private val misfitsFunction: MisfitsFunction
) {

    fun targetFunctionValue(signals: List<ExperimentalData>, model: List<ModelLayer>): Double {
        return targetFunction(
            forwardSolver(signals, model),
            signals.map { it.resistanceApparent },
            signals.map { it.errorResistanceApparent }
        )
    }

    fun errorValue(signals: List<ExperimentalData>, model: List<ModelLayer>): List<Double> {
        return misfitsFunction(signals, model)
    }

    fun errorAvgMax(signals: List<ExperimentalData>, model: List<ModelLayer>): AvgMax {
        val error = errorValue(signals, model)
        val avg = error.map { abs(it) }.average()
        val max = error.maxOfOrNull { abs(it) } ?: 0.0
        return AvgMax(avg, max)
    }

    fun misfitsValue(signals: List<ExperimentalData>, model: List<ModelLayer>): List<Double> {
        val theor = forwardSolver(signals, model)
        val exp = signals.map { it.resistanceApparent }
        return exp.mapIndexed { idx, resApp -> ((resApp - theor[idx]) / resApp) * 100 }
    }

    fun misfitsAvgMax(signals: List<ExperimentalData>, model: List<ModelLayer>): AvgMax {
        val misfits = misfitsValue(signals, model)
        val avg = misfits.map { abs(it) }.average()
        val max = misfits.maxOfOrNull { abs(it) } ?: 0.0
        return AvgMax(avg, max)
    }

}

data class AvgMax(val avg: Double, val max: Double)
