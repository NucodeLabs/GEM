package ru.nucodelabs.geo.ves.calc.graph

import ru.nucodelabs.geo.ves.ReadOnlyExperimentalSignal
import ru.nucodelabs.geo.ves.ReadOnlyModelLayer

interface MisfitsFunction {
    /**
     * Returns list of misfits between experimental and theoretical curves
     */
    operator fun invoke(
        experimentalData: List<ReadOnlyExperimentalSignal>,
        modelData: List<ReadOnlyModelLayer>
    ): List<Double>
}