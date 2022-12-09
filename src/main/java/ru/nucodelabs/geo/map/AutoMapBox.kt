package ru.nucodelabs.geo.map

import kotlin.math.abs

class AutoMapBox(
    val center: Wsg,
    private val maxAb2WithAzimuth: Collection<AzimuthPoint>
) {
    private val maxAbsXFromCenterInMeters: Double by lazy {
        maxAb2WithAzimuth.maxOf { abs(xFromCenter(it.distFromCenterInMeters, it.azimuthInDegrees)) }
    }

    private val maxAbsYFromCenterInMeters: Double by lazy {
        maxAb2WithAzimuth.maxOf { abs(yFromCenter(it.distFromCenterInMeters, it.azimuthInDegrees)) }
    }

    val bottomLeftCorner: Wsg by lazy {
        center + Dx(-maxAbsXFromCenterInMeters) + Dy(-maxAbsYFromCenterInMeters)
    }

    val upperRightCorner: Wsg by lazy {
        center + Dx(maxAbsXFromCenterInMeters) + Dy(maxAbsYFromCenterInMeters)
    }
}

data class AzimuthPoint(val distFromCenterInMeters: Double, val azimuthInDegrees: Double)