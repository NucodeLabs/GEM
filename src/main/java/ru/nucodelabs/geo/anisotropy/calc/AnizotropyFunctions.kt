package ru.nucodelabs.geo.anisotropy.calc

import jakarta.validation.Valid
import ru.nucodelabs.geo.anisotropy.AzimuthSignals
import ru.nucodelabs.geo.anisotropy.ModelLayer
import ru.nucodelabs.mathves.AnizotropyFunctions

fun forwardSolveAnizotropy(azimuthSignals: MutableList<@Valid AzimuthSignals>, model: MutableList<@Valid ModelLayer>) {
    val nSignals = azimuthSignals.map { it.signals }.map { it.effectiveSignals }.size.toShort()
    AnizotropyFunctions.signalModelingWithAzimuthSchlumberger(
        azimuthSignals.size.toShort(),
        azimuthSignals.map { it.azimuth }.toDoubleArray(),
        nSignals,
        azimuthSignals.map { it.signals }.map { it.effectiveSignals }.map { it.indexOf() }
    )
}