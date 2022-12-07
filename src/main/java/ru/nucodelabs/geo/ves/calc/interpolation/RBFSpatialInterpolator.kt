package ru.nucodelabs.geo.ves.calc.interpolation

import smile.interpolation.RBFInterpolation2D
import smile.math.rbf.GaussianRadialBasis
import smile.math.rbf.MultiquadricRadialBasis
import smile.math.rbf.ThinPlateRadialBasis

class RBFSpatialInterpolator(x: DoubleArray, y: DoubleArray, f: DoubleArray): SpatialInterpolator {

    private var rbfInterpolation2D: RBFInterpolation2D

    init {
        rbfInterpolation2D = RBFInterpolation2D(x, y, f, ThinPlateRadialBasis())
    }
    override fun interpolate(x: Double, y: Double): Double {
        return rbfInterpolation2D.interpolate(x, y)
    }

}
