package ru.nucodelabs.geo.ves.calc.interpolation

interface Interpolator2D {
    fun getValue(x: Double, y: Double): Double
}