package ru.nucodelabs.geo.forward

interface ForwardSolver {
    operator fun invoke(
        experimentalAb2: List<Double>,
        experimentalMn2: List<Double>,
        modelPower: List<Double>,
        modelResistance: List<Double>,
    ): List<Double>
}