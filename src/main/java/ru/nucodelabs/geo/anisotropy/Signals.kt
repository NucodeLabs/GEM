package ru.nucodelabs.geo.anisotropy

import jakarta.validation.Valid
import ru.nucodelabs.geo.anisotropy.calc.processSignals

class Signals(
    signals: List<Signal> = listOf()
) {
    val sortedSignals: List<@Valid Signal> = processSignals(signals)

    val effectiveSignals: List<@Valid Signal>
        get() = sortedSignals.filter { !it.isHidden }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Signals

        if (sortedSignals != other.sortedSignals) return false

        return true
    }

    override fun hashCode(): Int {
        return sortedSignals.hashCode()
    }
}