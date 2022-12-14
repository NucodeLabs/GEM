package ru.nucodelabs.geo.ves.calc.interpolation

interface SpatialInterpolator {
    fun interpolate(x: Double, y: Double): Double

    fun build(x: DoubleArray, y: DoubleArray, f: DoubleArray)
}