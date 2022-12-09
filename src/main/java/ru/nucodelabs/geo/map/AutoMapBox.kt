package ru.nucodelabs.geo.map

import kotlin.math.abs

/**
 * Calculates map box according to center and AB/2 distances with their azimuths
 * @param center center of inst
 * @param maxAb2WithAzimuth collection that contains only abs max AB/2 points with their azimuth
 */
class AutoMapBox(
    val center: Wsg,
    private val maxAb2WithAzimuth: Collection<AzimuthPoint>,
    paddingPercent: Double = 20.0
) {
    private val maxAbsXFromCenterInMeters: Double by lazy {
        maxAb2WithAzimuth.maxOf { abs(xFromCenter(it.distFromCenterInMeters, it.azimuthInDegrees)) }
    }

    private val maxAbsYFromCenterInMeters: Double by lazy {
        maxAb2WithAzimuth.maxOf { abs(yFromCenter(it.distFromCenterInMeters, it.azimuthInDegrees)) }
    }

    private val padCoefficient = 1 + paddingPercent

    val bottomLeftCorner: Wsg by lazy {
        center + Dx(-maxAbsXFromCenterInMeters * padCoefficient) + Dy(-maxAbsYFromCenterInMeters * padCoefficient)
    }

    val upperRightCorner: Wsg by lazy {
        center + Dx(maxAbsXFromCenterInMeters * padCoefficient) + Dy(maxAbsYFromCenterInMeters * padCoefficient)
    }
}

data class AzimuthPoint(val distFromCenterInMeters: Double, val azimuthInDegrees: Double)