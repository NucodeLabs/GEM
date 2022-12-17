package ru.nucodelabs.geo.map

import kotlin.math.abs

/**
 * Calculates map box according to center and AB/2 distances with their azimuths
 * @param center center of inst
 * @param maxAb2WithAzimuth collection that contains only abs max AB/2 points with their azimuth
 */
class MapSizer(
    val center: WGS,
    private val maxAb2WithAzimuth: Collection<AzimuthPoint>,
    val scale: Double
) {
    /**
     * Maximal absolute distance by X from center that points take in meters
     */
    val maxAbsXFromCenterInMeters: Double by lazy {
        maxAb2WithAzimuth.maxOf { abs(xFromCenter(it.distFromCenterInMeters, it.azimuthInDegrees)) }
    }

    /**
     * Maximal absolute distance by Y from center that points take in meters
     */
    val maxAbsYFromCenterInMeters: Double by lazy {
        maxAb2WithAzimuth.maxOf { abs(yFromCenter(it.distFromCenterInMeters, it.azimuthInDegrees)) }
    }

    /**
     * Maximal absolute distance by Y from center that points take in meters with scale applied
     */
    val maxAbsYFromCenterScaledInMeters: Double by lazy {
        maxAbsYFromCenterInMeters * coefficient
    }

    /**
     * Maximal absolute distance by X from center that points take in meters with scale applied
     */
    val maxAbsXFromCenterScaledInMeters: Double by lazy {
        maxAbsXFromCenterInMeters * coefficient
    }

    private val coefficient = 1.0 / scale

    /**
     * Calculated bottom left corner of rectangle in WSG
     */
    val bottomLeftCorner: WGS by lazy {
        center - Offset(maxAbsXFromCenterInMeters * coefficient, maxAbsYFromCenterInMeters * coefficient)
    }

    /**
     * Calculated upper right corner of rectangle in WSG
     */
    val upperRightCorner: WGS by lazy {
        center + Offset(maxAbsXFromCenterInMeters * coefficient, maxAbsYFromCenterInMeters * coefficient)
    }
}

/**
 * Stores point coordinates using distance from center and azimuthal angle
 */
data class AzimuthPoint(val distFromCenterInMeters: Double, val azimuthInDegrees: Double)