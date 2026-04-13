package ru.nucodelabs.geo.anisotropy.calc

import ru.nucodelabs.geo.anisotropy.AzimuthSignals

data class SignalRelation(
    val azimuth: Double,
    val relations: List<Relation>,
)

data class Relation(
    val ab2: Double,
    val value: Double,
)

fun signalsRelations(current: AzimuthSignals, all: List<AzimuthSignals>): List<SignalRelation> {
    val other = (all - current).filter {
        it.signals.effectiveSignals.size == current.signals.effectiveSignals.size
    }
    return other.map {
        SignalRelation(
            it.azimuth,
            it.signals.effectiveSignals.mapIndexed { index, signal ->
                Relation(
                    signal.ab2,
                    current.signals.effectiveSignals[index].resistivityApparent / signal.resistivityApparent
                )
            })
    }
}