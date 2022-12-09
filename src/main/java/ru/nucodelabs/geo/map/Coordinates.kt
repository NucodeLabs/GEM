package ru.nucodelabs.geo.map

import java.lang.Math.toRadians
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * @param distInMeters distance between point and center in **meters**
 * @param azimuthInDegrees azimuth of point in **degrees**
 * @return X-coordinate of point relative to center in **meters**
 */
fun xFromCenter(distInMeters: Double, azimuthInDegrees: Double): Double {
    return distInMeters * cos(toRadians(azimuthInDegrees % 90.0)) * xSignForAzimuth(azimuthInDegrees)
}

/**
 * @param distInMeters distance between point and center in **meters**
 * @param azimuthInDegrees azimuth of point in **degrees**
 * @return Y-coordinate of point relative to center in **meters**
 */
fun yFromCenter(distInMeters: Double, azimuthInDegrees: Double): Double {
    return distInMeters * sin(toRadians(azimuthInDegrees % 90.0)) * ySignForAzimuth(azimuthInDegrees)
}

private fun xSignForAzimuth(angleInDegrees: Double): Double {
    return when (angleInDegrees) {
        in 0.0..180.0 -> +1.0
        else -> -1.0
    }
}

private fun ySignForAzimuth(angleInDegrees: Double): Double {
    return when (angleInDegrees) {
        in 90.0..270.0 -> -1.0
        else -> +1.0
    }
}

private const val R_EARTH = 6378.0

data class Wsg(val longitudeInDegrees: Double, val latitudeInDegrees: Double)
data class Dx(val distanceInMeters: Double)
data class Dy(val distanceInMeters: Double)

operator fun Wsg.plus(dx: Dx): Wsg {
    val newLon =
        longitudeInDegrees + (dx.distanceInMeters * 1e-3 / R_EARTH) * (180 / PI) / cos(latitudeInDegrees * PI / 180)
    return copy(longitudeInDegrees = newLon)
}

operator fun Wsg.plus(dy: Dy): Wsg {
    val newLat = latitudeInDegrees + (dy.distanceInMeters * 1e-3 / R_EARTH) * (180 / PI)
    return copy(latitudeInDegrees = newLat)
}
