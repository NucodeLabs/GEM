package ru.nucodelabs.geo.ves.calc.adapter

import ru.nucodelabs.geo.forward.ForwardSolver
import ru.nucodelabs.geo.ves.ReadOnlyExperimentalSignal
import ru.nucodelabs.geo.ves.ReadOnlyModelLayer

operator fun ForwardSolver.invoke(
    experimentalData: List<ReadOnlyExperimentalSignal>,
    modelData: List<ReadOnlyModelLayer>
): List<Double> {
    return this(
        experimentalData.map { it.ab2 },
        experimentalData.map { it.mn2 },
        modelData.map { it.power },
        modelData.map { it.resistivity },
    )
}
