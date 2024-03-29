package ru.nucodelabs.geo.ves.calc.interpolation

import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction

interface Interpolator1D {
    fun interpolate(x: DoubleArray, f: DoubleArray): PolynomialSplineFunction
}