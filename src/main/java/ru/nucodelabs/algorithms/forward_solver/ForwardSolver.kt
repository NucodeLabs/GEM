package ru.nucodelabs.algorithms.forward_solver

import ru.nucodelabs.data.ves.ExperimentalData
import ru.nucodelabs.data.ves.ModelLayer

interface ForwardSolver : (List<ExperimentalData>, List<ModelLayer>) -> List<Double>

fun ForwardSolver(): ForwardSolver = SonetForwardSolver()