package ru.nucodelabs.geo.anisotropy

import jakarta.validation.Valid

data class Point(
    var azimuthSignals: MutableList<@Valid AzimuthSignals>
)
