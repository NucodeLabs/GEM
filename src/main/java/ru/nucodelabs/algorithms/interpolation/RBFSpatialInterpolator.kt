package ru.nucodelabs.algorithms.interpolation

import smile.interpolation.RBFInterpolation2D
import smile.math.rbf.GaussianRadialBasis

class RBFSpatialInterpolator(x: DoubleArray, y: DoubleArray, f: DoubleArray): SpatialInterpolator {

    private var rbfInterpolation2D: RBFInterpolation2D

    init {
        rbfInterpolation2D = RBFInterpolation2D(x, y, f, GaussianRadialBasis())
    }
    override fun interpolate(x: Double, y: Double): Double {
        return rbfInterpolation2D.interpolate(x, y)
    }

}
