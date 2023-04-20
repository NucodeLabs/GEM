package ru.nucodelabs.geo.target

object TargetFunction {
    interface WithoutError {
        fun apply(theoretical: List<Double>, experimental: List<Double>): Double
    }

    interface WithError {
        fun apply(theoretical: List<Double>, experimental: List<Double>, relativeError: List<Double>): Double
    }
}