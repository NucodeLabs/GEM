package ru.nucodelabs.geo.ves.calc.interpolation

import smile.interpolation.RBFInterpolation2D
import smile.math.rbf.ThinPlateRadialBasis

class RBFSpatialInterpolator: SpatialInterpolator {

    private lateinit var rbfInterpolation2D: RBFInterpolation2D

    override fun interpolate(x: Double, y: Double): Double {
        return rbfInterpolation2D.interpolate(x, y)
    }

    override fun build(x: DoubleArray, y: DoubleArray, f: DoubleArray) {
        rbfInterpolation2D = RBFInterpolation2D(x, y, f, ThinPlateRadialBasis())
    }

}
