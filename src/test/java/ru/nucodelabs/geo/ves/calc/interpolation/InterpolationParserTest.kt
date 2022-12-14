package ru.nucodelabs.geo.ves.calc.interpolation

import org.junit.jupiter.api.Test

internal class InterpolationParserTest {

    @Test
    fun parseSpatial() {
        val x = DoubleArray(5)
        val y = DoubleArray(5)
        val f = DoubleArray(5)
        x[0] = 1.0
        x[1] = 2.0
        x[2] = 3.0
        x[3] = 1.0
        x[4] = 3.0
        y[0] = 1.0
        y[1] = 2.0
        y[2] = 3.0
        y[3] = 3.0
        y[4] = 1.0
        f[0] = 1.0
        f[1] = 2.0
        f[2] = 3.0
        f[3] = 3.0
        f[4] = 3.0
        val spatialInterpolator: SpatialInterpolator = RBFSpatialInterpolator()
        spatialInterpolator.build(x, y, f)
        val t = spatialInterpolator.interpolate(1.0, 1.5)
        println(t)
    }
}