package ru.nucodelabs.geo.ves.calc.interpolation

import org.apache.commons.math3.analysis.interpolation.BicubicInterpolatingFunction

interface RegularGridInterpolator {
    fun interpolate(x: DoubleArray, y: DoubleArray, f: Array<DoubleArray>): BicubicInterpolatingFunction
}