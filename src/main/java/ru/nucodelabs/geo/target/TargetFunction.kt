package ru.nucodelabs.geo.target

object TargetFunction {
//    interface WithoutError {
//        operator fun invoke(theoretical: List<Double>, experimental: List<Double>): Double
//    }

    interface WithError {
        operator fun invoke(theoretical: List<Double>, experimental: List<Double>, relativeError: List<Double>): Double
    }
}