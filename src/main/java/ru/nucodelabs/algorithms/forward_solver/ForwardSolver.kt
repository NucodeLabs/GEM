package ru.nucodelabs.algorithms.forward_solver

import ru.nucodelabs.data.ves.ExperimentalData
import ru.nucodelabs.data.ves.ModelLayer

interface ForwardSolver : (List<ExperimentalData>, List<ModelLayer>) -> List<Double> {
    companion object Factory {
        @JvmStatic
        fun createDefault(): ForwardSolver = ForwardSolver()
    }
}

fun ForwardSolver(): ForwardSolver = SonetForwardSolver()