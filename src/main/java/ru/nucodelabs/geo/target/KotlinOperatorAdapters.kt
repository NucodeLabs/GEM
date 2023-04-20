package ru.nucodelabs.geo.target

operator fun TargetFunction.WithError.invoke(
    theoretical: List<Double>,
    experimental: List<Double>,
    relativeError: List<Double>
) = apply(theoretical, experimental, relativeError)

operator fun TargetFunction.WithoutError.invoke(
    theoretical: List<Double>,
    experimental: List<Double>,
) = apply(theoretical, experimental)