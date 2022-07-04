package ru.nucodelabs.algorithms.charts

import ru.nucodelabs.algorithms.forward_solver.ForwardSolver
import ru.nucodelabs.data.ves.ExperimentalData
import ru.nucodelabs.data.ves.ModelLayer

interface MisfitValuesFactory : (List<ExperimentalData>, List<ModelLayer>) -> List<Double>

fun MisfitValuesFactory(forwardSolver: ForwardSolver): MisfitValuesFactory = MisfitValuesFactoryNative(forwardSolver)