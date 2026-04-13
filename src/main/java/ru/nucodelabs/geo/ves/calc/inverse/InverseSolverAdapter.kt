package ru.nucodelabs.geo.ves.calc.inverse

import ru.nucodelabs.geo.ves.ModelLayer
import ru.nucodelabs.geo.ves.ReadOnlyExperimentalSignal
import ru.nucodelabs.geo.ves.ReadOnlyModelLayer

operator fun InverseSolver.invoke(
    experimentalSignals: List<ReadOnlyExperimentalSignal>,
    initialModel: List<ReadOnlyModelLayer>,
    maxEval: Int = InverseSolver.MAX_EVAL_DEFAULT
): List<ModelLayer> = getOptimizedModelData(experimentalSignals, initialModel, maxEval)