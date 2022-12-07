package ru.nucodelabs.geo.ves.calc.interpolation

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction

class ApacheInterpolator1D: Interpolator1D {
    override fun interpolate(x: DoubleArray, f: DoubleArray): PolynomialSplineFunction {
        val splineInterpolator = SplineInterpolator()
        return splineInterpolator.interpolate(x, f)
    }
}