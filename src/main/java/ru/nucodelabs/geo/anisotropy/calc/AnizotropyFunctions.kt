package ru.nucodelabs.geo.anisotropy.calc

import jakarta.validation.Valid
import ru.nucodelabs.geo.anisotropy.AzimuthSignals
import ru.nucodelabs.geo.anisotropy.ModelLayer
import ru.nucodelabs.mathves.AnizotropyFunctions

fun forwardSolveAnizotropy(
    azimuthSignals: MutableList<@Valid AzimuthSignals>,
    model: MutableList<@Valid ModelLayer>
): MutableList<Double> {
    val signalsOut = azimuthSignals.map { it.signals.sortedSignals }.flatten().map { 0.0 }.toDoubleArray()
    val nSignals = signalsOut.size.toShort()
    if (AnizotropyFunctions.signalModelingWithAzimuthSchlumberger(
            azimuthSignals.size.toShort(),
            azimuthSignals.map { it.azimuth }.toDoubleArray(),
            nSignals,
            azimuthSignals.mapIndexed { index, azimuthSignalsMap ->
                List(azimuthSignalsMap.signals.effectiveSignals.size) { index + 1 }
            }.flatten().map {
                it.toShort()
            }.toShortArray(),
            azimuthSignals.map { it.signals.effectiveSignals.map { it.ab2 } }.flatten().toDoubleArray(),
            azimuthSignals.map { it.signals.effectiveSignals.map { it.mn2 } }.flatten().toDoubleArray(),
            signalsOut,
            model.size.toShort(),
            model.map { it.power }.toDoubleArray(),
            model.map { it.resistance }.toDoubleArray(),
            model.map { it.verticalAnisotropyCoefficient }.toDoubleArray(),
            model.map { it.azimuth }.toDoubleArray(),
            model.map { it.azimuthAnisotropyCoefficient }.toDoubleArray()
        ) != 0
    ) {
        throw RuntimeException("forwardSolveAnizotropy error")
    }

    return signalsOut.toMutableList()
}