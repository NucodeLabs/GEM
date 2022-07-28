package ru.nucodelabs.algorithms.forward_solver

import ru.nucodelabs.data.ves.ExperimentalData
import ru.nucodelabs.data.ves.ModelLayer
import ru.nucodelabs.mathves.SonetForwardSolver

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

fun ForwardSolver(): ForwardSolver = object : ForwardSolver {
    private val delegate = SonetForwardSolver()
    override fun invoke(experimentalData: List<ExperimentalData>, modelData: List<ModelLayer>): List<Double> {
        return delegate.solve(
            modelData.map { it.resistance }.toDoubleArray(),
            modelData.map { it.power }.toDoubleArray(),
            modelData.size,
            experimentalData.map { it.ab2 }.toDoubleArray(),
            experimentalData.map { it.mn2 }.toDoubleArray(),
            experimentalData.size
        ).toList()
    }
}