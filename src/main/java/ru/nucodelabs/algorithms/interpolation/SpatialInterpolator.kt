package ru.nucodelabs.algorithms.interpolation

interface SpatialInterpolator {
    fun interpolate(x: Double, y: Double): Double
}