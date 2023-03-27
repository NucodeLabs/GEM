package ru.nucodelabs.geo.anisotropy

import jakarta.validation.Valid
import ru.nucodelabs.gem.util.std.AutoEquals
import ru.nucodelabs.geo.anisotropy.calc.processSignals

class Signals(
    signals: List<Signal> = listOf()
) : AutoEquals() {
    init {
        includeInEquals(
            { sortedSignals }
        )
    }

    val sortedSignals: List<@Valid Signal> = processSignals(signals)

    val effectiveSignals: List<@Valid Signal>
        get() = sortedSignals.filter { !it.isHidden }
}