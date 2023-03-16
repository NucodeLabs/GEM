package ru.nucodelabs.geo.anisotropy

import jakarta.validation.Valid

data class Point(
    val azimuthSignals: List<@Valid AzimuthSignals>
)
