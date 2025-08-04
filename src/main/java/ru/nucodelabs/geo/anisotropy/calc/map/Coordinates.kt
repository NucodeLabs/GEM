package ru.nucodelabs.geo.anisotropy.calc.map

import java.lang.Math.toDegrees
import java.lang.Math.toRadians
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

fun mirror(angleInDegrees: Double) = (angleInDegrees + 180.0) % 360.0

operator fun Double.not() = mirror(this)

private fun toComplexAngle(angleInDegrees: Double): Double {
    return -angleInDegrees + 90
}

private const val R_EARTH = 6_378_137

data class Wgs(val longitudeInDegrees: Double, val latitudeInDegrees: Double)

data class Offset(val dXInMeters: Double = 0.0, val dYInMeters: Double = 0.0)

data class Point(val x: Int = 0, val y: Int = 0)

operator fun Wgs.plus(offset: Offset): Wgs {
    //Coordinate offsets in radians
    val dLat = offset.dYInMeters / R_EARTH
    val dLon = offset.dXInMeters / (R_EARTH * cos(toRadians(this.latitudeInDegrees)))

    //OffsetPosition, decimal degrees
    val newLat = this.latitudeInDegrees + toDegrees(dLat)
    val newLon = this.longitudeInDegrees + toDegrees(dLon)

    return this.copy(longitudeInDegrees = newLon, latitudeInDegrees = newLat)
}

operator fun Wgs.minus(offset: Offset): Wgs {
    //Coordinate offsets in radians
    val dLat = offset.dYInMeters / R_EARTH
    val dLon = offset.dXInMeters / (R_EARTH * cos(toRadians(this.latitudeInDegrees)))

    //OffsetPosition, decimal degrees
    val newLat = this.latitudeInDegrees - toDegrees(dLat)
    val newLon = this.longitudeInDegrees - toDegrees(dLon)

    return this.copy(longitudeInDegrees = newLon, latitudeInDegrees = newLat)
}


