package ru.nucodelabs.geo.ves.calc.interpolation

import smile.interpolation.KrigingInterpolation2D

class KrigingInterpolator: SpatialInterpolator {
    private lateinit var krigingInterpolation2D: KrigingInterpolation2D

    override fun interpolate(x: Double, y: Double): Double {
        return this.krigingInterpolation2D.interpolate(x, y)
    }

    override fun build(x: DoubleArray, y: DoubleArray, f: DoubleArray) {
        this.krigingInterpolation2D = KrigingInterpolation2D(x, y, f)
    }
}