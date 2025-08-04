package ru.nucodelabs.geo.ves.calc.adapter

import ru.nucodelabs.geo.forward.ForwardSolver
import ru.nucodelabs.geo.ves.ExperimentalData
import ru.nucodelabs.geo.ves.ModelLayer

operator fun ForwardSolver.invoke(experimentalData: List<ExperimentalData>, modelData: List<ModelLayer>): List<Double> {
    return this(
        experimentalData.map { it.ab2 },
        experimentalData.map { it.mn2 },
        modelData.map { it.power },
        modelData.map { it.resistance },
    )
}
