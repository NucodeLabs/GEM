package ru.nucodelabs.algorithms.forward_solver

import ru.nucodelabs.data.ves.ExperimentalData
import ru.nucodelabs.data.ves.ModelLayer

interface ForwardSolver {
    /**
     * Returns solved resistance values for `modelData` that match to distances of `experimentalData`
     */
    operator fun invoke(experimentalData: List<ExperimentalData>, modelData: List<ModelLayer>): List<Double>

    companion object Factory {
        @JvmStatic
        fun createDefault(): ForwardSolver = ForwardSolver()
    }
}

fun ForwardSolver(): ForwardSolver = SonetForwardSolver()