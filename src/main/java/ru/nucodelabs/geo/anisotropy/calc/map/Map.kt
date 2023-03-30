package ru.nucodelabs.geo.anisotropy.calc.map

import ru.nucodelabs.geo.anisotropy.AzimuthSignals

fun maxAb2WithAzimuth(azimuthSignals: List<AzimuthSignals>): List<AzimuthPoint> {
    return azimuthSignals.map { azimuth ->
        AzimuthPoint(
            azimuthInDegrees = azimuth.azimuth,
            distFromCenterInMeters = azimuth.signals.sortedSignals.maxOf { it.ab2 }
        )
    }
}