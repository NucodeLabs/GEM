package ru.nucodelabs.algorithms.charts

import ru.nucodelabs.algorithms.forward_solver.ForwardSolver
import ru.nucodelabs.data.ves.ExperimentalData
import ru.nucodelabs.data.ves.ModelLayer

interface MisfitsFunction {
    /**
     * Returns list of misfits between experimental and theoretical curves
     */
    operator fun invoke(experimentalData: List<ExperimentalData>, modelData: List<ModelLayer>): List<Double>

    companion object Factory {
        @JvmStatic
        fun createDefault(forwardSolver: ForwardSolver): MisfitsFunction = MisfitsFunction(forwardSolver)
    }
}

fun MisfitsFunction(forwardSolver: ForwardSolver): MisfitsFunction = MisfitValuesFactoryNative(forwardSolver)