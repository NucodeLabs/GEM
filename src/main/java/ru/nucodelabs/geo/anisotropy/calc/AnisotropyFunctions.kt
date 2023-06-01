package ru.nucodelabs.geo.anisotropy.calc

import ru.nucodelabs.geo.anisotropy.AzimuthSignals
import ru.nucodelabs.geo.anisotropy.FixableValue
import ru.nucodelabs.geo.anisotropy.ModelLayer
import ru.nucodelabs.geo.anisotropy.Signals
import ru.nucodelabs.mathves.AnizotropyFunctions

fun forwardSolve(
    azimuthSignalsList: List<AzimuthSignals>,
    model: List<ModelLayer>
): List<AzimuthSignals> {
    if (azimuthSignalsList.isEmpty() || model.isEmpty()) {
        return emptyList()
    }
    val signalsOut = azimuthSignalsList.map { it.signals.effectiveSignals }.flatten().map { 0.0 }.toDoubleArray()
    val nSignals = signalsOut.size.toShort()
    if (AnizotropyFunctions.signalModelingWithAzimuthSchlumberger(
            azimuthSignalsList.size.toShort(),
            azimuthSignalsList.map { it.azimuth }.toDoubleArray(),
            nSignals,
            azimuthSignalsList.mapIndexed { index, azimuthSignalsMap ->
                List(azimuthSignalsMap.signals.effectiveSignals.size) { index }
            }.flatten().map {
                it.toShort()
            }.toShortArray(),
            azimuthSignalsList.map { azimuthSignals -> azimuthSignals.signals.effectiveSignals.map { it.ab2 } }
                .flatten().toDoubleArray(),
            azimuthSignalsList.map { azimuthSignals -> azimuthSignals.signals.effectiveSignals.map { it.mn2 } }
                .flatten().toDoubleArray(),
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

    var i = 0
    return azimuthSignalsList.map {
        it.copy(
            signals = Signals(
                it.signals.sortedSignals.map { signal ->
                    val new = signal.copy(resistanceApparent = signalsOut[i])
                    i++
                    new
                }
            )
        )
    }
}

fun inverseSolveInPlace(
    azimuthSignals: List<AzimuthSignals>,
    model: List<ModelLayer>
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
    val fix_h = model.map { if (it.power.isFixed) 1.toByte() else 0.toByte() }.toByteArray()
    val h = model.map { it.power.value }.toDoubleArray()
    val fix_ro = model.map { if (it.resistance.isFixed) 1.toByte() else 0.toByte() }.toByteArray()
    val ro_avg = model.map { it.resistance.value }.toDoubleArray()
    val fix_kanisotropy_vert =
        model.map { if (it.verticalAnisotropyCoefficient.isFixed) 1.toByte() else 0.toByte() }.toByteArray()
    val kanisotropy_vert = model.map { it.verticalAnisotropyCoefficient.value }.toDoubleArray()
    val fix_azimuth = model.map { if (it.azimuth.isFixed) 1.toByte() else 0.toByte() }.toByteArray()
    val azimuth = model.map { it.azimuth.value }.toDoubleArray()
    val fix_kanisotropy_azimuth =
        model.map { if (it.azimuthAnisotropyCoefficient.isFixed) 1.toByte() else 0.toByte() }.toByteArray()
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

fun convertModel(
    models: List<List<ru.nucodelabs.geo.ves.ModelLayer>>,
    azimuths: List<Double>
): List<ModelLayer> {
    val singleLayer = models.first()
    val allLayers = models.flatten()

    val n_layers = singleLayer.size
    val h = singleLayer.map { it.power }.toDoubleArray()
    val n_model = models.size
    val model_azimuth = azimuths.toDoubleArray()
    val ro_isotrop = allLayers.map { it.resistance }.toDoubleArray()

    val ro_avg = List(n_layers) { 0.0 }.toDoubleArray()
    val kanisotropy_vert = List(n_layers) { 0.0 }.toDoubleArray()
    val azimuth = List(n_layers) { 0.0 }.toDoubleArray()
    val kanisotropy_azimuth = List(n_layers) { 0.0 }.toDoubleArray()

    val errorCode = AnizotropyFunctions.startModelWithAzimuthFromIsotrop(
        n_layers.toShort(),
        h,
        n_model.toShort(),
        model_azimuth,
        ro_isotrop,
        ro_avg,
        kanisotropy_vert,
        azimuth,
        kanisotropy_azimuth
    )

    if (errorCode != 0) {
        throw RuntimeException("inverseSolveAnizotropy error code is $errorCode")
    }

    val anizotropyModel: MutableList<ModelLayer> = arrayListOf()
    for (i in 0 until n_layers) {
        anizotropyModel.add(
            ModelLayer(
                power = FixableValue(h[i], false),
                resistance = FixableValue(ro_avg[i], false),
                verticalAnisotropyCoefficient = FixableValue(kanisotropy_vert[i], false),
                azimuth = FixableValue(azimuth[i], false),
                azimuthAnisotropyCoefficient = FixableValue(kanisotropy_azimuth[i], false)
            )
        )
    }

    return anizotropyModel
}