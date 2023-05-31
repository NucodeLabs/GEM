package ru.nucodelabs.geo.forward.impl

import ru.nucodelabs.geo.forward.ForwardSolver
import ru.nucodelabs.mathves.SonetForwardSolver

class SonetForwardSolverAdapter : ForwardSolver {
    private val solver = SonetForwardSolver()

    override fun invoke(
        experimentalAb2: List<Double>,
        experimentalMn2: List<Double>,
        modelPower: List<Double>,
        modelResistance: List<Double>
    ): List<Double> {
        return solver.solve(
            modelResistance.toDoubleArray(),
            modelPower.toDoubleArray(),
            modelPower.size,
            experimentalMn2.toDoubleArray(),
            experimentalMn2.toDoubleArray(),
            experimentalAb2.size
        ).toList()
    }

}