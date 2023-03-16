package ru.nucodelabs.geo.anisotropy

import jakarta.validation.Valid
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin

data class AzimuthSignals(
    @DecimalMin("0") @DecimalMax("360") val azimuth: Double,
    val signals: List<@Valid Signal>
)
