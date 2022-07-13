package ru.nucodelabs.algorithms.interpolation

import org.apache.commons.math3.analysis.interpolation.BicubicInterpolatingFunction
import org.apache.commons.math3.analysis.interpolation.BicubicInterpolator

class ApacheInterpolator2D: RegularGridInterpolator {
    override fun interpolate(x: DoubleArray, y: DoubleArray, f: Array<DoubleArray>): BicubicInterpolatingFunction {
        val bicubicInterpolator = BicubicInterpolator()
        return bicubicInterpolator.interpolate(x, y, f)
    }
}