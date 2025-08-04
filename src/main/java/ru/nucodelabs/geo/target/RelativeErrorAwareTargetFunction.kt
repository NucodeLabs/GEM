package ru.nucodelabs.geo.target

fun interface RelativeErrorAwareTargetFunction {
    operator fun invoke(theoretical: List<Double>, experimental: List<Double>, relativeError: List<Double>): Double
}