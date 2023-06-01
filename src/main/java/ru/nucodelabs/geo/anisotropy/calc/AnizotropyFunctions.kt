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
                List(azimuthSignalsMap.signals.effectiveSignals.size) { index }
            }.flatten().map {
                it.toShort()
            }.toShortArray(),
            azimuthSignals.map { it.signals.effectiveSignals.map { it.ab2 } }.flatten().toDoubleArray(),
            azimuthSignals.map { it.signals.effectiveSignals.map { it.mn2 } }.flatten().toDoubleArray(),
            signalsOut,
            model.size.toShort(),
            model.map { it.power.value }.toDoubleArray(),
            model.map { it.resistance.value }.toDoubleArray(),
            model.map { it.verticalAnisotropyCoefficient.value }.toDoubleArray(),
            model.map { it.azimuth.value }.toDoubleArray(),
            model.map { it.azimuthAnisotropyCoefficient.value }.toDoubleArray()
        ) != 0
    ) {
        throw RuntimeException("forwardSolveAnizotropy error")
    }

    return signalsOut.toMutableList()
}

fun inverseSolveAnizotropy(
    azimuthSignals: MutableList<@Valid AzimuthSignals>,
    model: MutableList<@Valid ModelLayer>,
    fixKanisotropy_vert: List<Boolean>,
): MutableList<Double> {
    val n_azimuth = azimuthSignals.size
    val signals_azimuth = azimuthSignals.map { it.azimuth }.toDoubleArray()
    val signals = azimuthSignals.map { it.signals.effectiveSignals }.flatten()
    val n_signals = signals.size
    val idx_azimuth = azimuthSignals.mapIndexed { index, azimuthSignalsMap ->
        List(azimuthSignalsMap.signals.effectiveSignals.size) { index }
    }.flatten().map {
        it.toShort()
    }.toShortArray()
    val AB2 = signals.map { it.ab2 }
    val MN2 = signals.map { it.mn2 }
    val rel_error = signals.map { it.errorResistanceApparent / 100.0 }
    val n_layers = model.size

    return arrayListOf()
}