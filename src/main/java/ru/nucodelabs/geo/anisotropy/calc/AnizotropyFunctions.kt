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
    model: MutableList<@Valid ModelLayer>
) {
    val n_azimuth = azimuthSignals.size.toShort()
    val signals_azimuth = azimuthSignals.map { it.azimuth }.toDoubleArray()
    val signals = azimuthSignals.map { it.signals.effectiveSignals }.flatten()
    val n_signals = signals.size.toShort()
    val idx_azimuth = azimuthSignals.mapIndexed { index, azimuthSignalsMap ->
        List(azimuthSignalsMap.signals.effectiveSignals.size) { index }
    }.flatten().map {
        it.toByte()
    }.toByteArray()
    val AB2 = signals.map { it.ab2 }.toDoubleArray()
    val MN2 = signals.map { it.mn2 }.toDoubleArray()
    val rel_error = signals.map { it.errorResistanceApparent / 100.0 }.toDoubleArray()

    val n_layers = model.size.toShort()
    val fix_h = model.map { if (it.power.isFixed == true) 1.toByte() else 0.toByte() }.toByteArray()
    val h = model.map { it.power.value }.toDoubleArray()
    val fix_ro = model.map { if (it.resistance.isFixed == true) 1.toByte() else 0.toByte() }.toByteArray()
    val ro_avg = model.map { it.resistance.value }.toDoubleArray()
    val fix_kanisotropy_vert =
        model.map { if (it.verticalAnisotropyCoefficient.isFixed == true) 1.toByte() else 0.toByte() }.toByteArray()
    val kanisotropy_vert = model.map { it.verticalAnisotropyCoefficient.value }.toDoubleArray()
    val fix_azimuth = model.map { if (it.azimuth.isFixed == true) 1.toByte() else 0.toByte() }.toByteArray()
    val azimuth = model.map { it.azimuth.value }.toDoubleArray()
    val fix_kanisotropy_azimuth =
        model.map { if (it.azimuthAnisotropyCoefficient.isFixed == true) 1.toByte() else 0.toByte() }.toByteArray()
    val kanisotropy_azimuth = model.map { it.azimuthAnisotropyCoefficient.value }.toDoubleArray()

    val errorCode = AnizotropyFunctions.inversionWithAzimuthSchlumberger(
        n_azimuth,
        signals_azimuth,
        n_signals,
        idx_azimuth,
        AB2,
        MN2,
        signals.map { it.resistanceApparent }.toDoubleArray(),
        rel_error,
        n_layers,
        fix_h,
        h,
        fix_ro,
        ro_avg,
        fix_kanisotropy_vert,
        kanisotropy_vert,
        fix_azimuth,
        azimuth,
        fix_kanisotropy_azimuth,
        kanisotropy_azimuth
    )

    if (errorCode != 0) {
        throw RuntimeException("inverseSolveAnizotropy error code is $errorCode")
    }

    model.forEachIndexed { i, layer ->
        layer.power.value = h[i]
        layer.resistance.value = ro_avg[i]
        layer.verticalAnisotropyCoefficient.value = kanisotropy_vert[i]
        layer.azimuth.value = azimuth[i]
        layer.azimuthAnisotropyCoefficient.value = kanisotropy_azimuth[i]
    }
}