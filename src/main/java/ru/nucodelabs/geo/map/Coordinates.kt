package ru.nucodelabs.geo.map

import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import java.util.DoubleSummaryStatistics
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * @param distInMeters distance between point and center in **meters**
 * @param azimuthInDegrees azimuth of point in **degrees**
 * @return X-coordinate of point relative to center in **meters**
 */
fun xFromCenter(distInMeters: Double, azimuthInDegrees: Double): Double {
    return distInMeters * cos(toRadians(toComplexAngle(azimuthInDegrees)))
}

/**
 * @param distInMeters distance between point and center in **meters**
 * @param azimuthInDegrees azimuth of point in **degrees**
 * @return Y-coordinate of point relative to center in **meters**
 */
fun yFromCenter(distInMeters: Double, azimuthInDegrees: Double): Double {
    return distInMeters * sin(toRadians(toComplexAngle(azimuthInDegrees)))
}

private fun toComplexAngle(angleInDegrees: Double): Double {
    return -angleInDegrees + 90
}

private const val R_EARTH = 6_378_137

data class WGS(val longitudeInDegrees: Double, val latitudeInDegrees: Double)

data class Offset(val dXInMeters: Double = 0.0, val dYInMeters: Double = 0.0)

operator fun WGS.plus(offset: Offset): WGS {
    //Coordinate offsets in radians
    val dLat = offset.dYInMeters / R_EARTH
    val dLon = offset.dXInMeters / (R_EARTH * cos(toRadians(this.latitudeInDegrees)))

    //OffsetPosition, decimal degrees
    val newLat = this.latitudeInDegrees + toDegrees(dLat)
    val newLon = this.longitudeInDegrees + toDegrees(dLon)

    return this.copy(longitudeInDegrees = newLon, latitudeInDegrees = newLat)
}

operator fun WGS.minus(offset: Offset): WGS {
    //Coordinate offsets in radians
    val dLat = offset.dYInMeters / R_EARTH
    val dLon = offset.dXInMeters / (R_EARTH * cos(toRadians(this.latitudeInDegrees)))

    //OffsetPosition, decimal degrees
    val newLat = this.latitudeInDegrees - toDegrees(dLat)
    val newLon = this.longitudeInDegrees - toDegrees(dLon)

    return this.copy(longitudeInDegrees = newLon, latitudeInDegrees = newLat)
}


