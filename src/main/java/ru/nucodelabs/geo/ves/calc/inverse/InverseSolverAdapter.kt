package ru.nucodelabs.geo.ves.calc.inverse

import ru.nucodelabs.geo.ves.ExperimentalData
import ru.nucodelabs.geo.ves.ModelLayer

operator fun InverseSolver.invoke(
    experimentalSignals: List<ExperimentalData>,
    initialModel: List<ModelLayer>,
    maxEval: Int = InverseSolver.MAX_EVAL_DEFAULT
): List<ModelLayer> = getOptimizedModelData(experimentalSignals, initialModel, maxEval)