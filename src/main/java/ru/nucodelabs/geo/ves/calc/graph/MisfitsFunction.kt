package ru.nucodelabs.geo.ves.calc.graph

import ru.nucodelabs.geo.ves.ExperimentalData
import ru.nucodelabs.geo.ves.ModelLayer

interface MisfitsFunction {
    /**
     * Returns list of misfits between experimental and theoretical curves
     */
    operator fun invoke(experimentalData: List<ExperimentalData>, modelData: List<ModelLayer>): List<Double>
}