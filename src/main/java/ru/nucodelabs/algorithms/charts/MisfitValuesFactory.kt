package ru.nucodelabs.algorithms.charts

import ru.nucodelabs.algorithms.forward_solver.ForwardSolver
import ru.nucodelabs.data.ves.ExperimentalData
import ru.nucodelabs.data.ves.ModelLayer

interface MisfitValuesFactory {
    /**
     * Returns list of misfits between experimental and theoretical curves
     */
    operator fun invoke(experimentalData: List<ExperimentalData>, modelData: List<ModelLayer>): List<Double>

    companion object Factory {
        @JvmStatic
        fun createDefault(forwardSolver: ForwardSolver): MisfitValuesFactory = MisfitValuesFactory(forwardSolver)
    }
}

fun MisfitValuesFactory(forwardSolver: ForwardSolver): MisfitValuesFactory = MisfitValuesFactoryNative(forwardSolver)