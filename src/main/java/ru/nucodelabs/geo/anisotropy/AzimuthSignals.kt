package ru.nucodelabs.geo.anisotropy

import jakarta.validation.Valid
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin

/**
 * Измерения для одного азимута
 * @property azimuth азимут
 * @property signals измеренные сигналы
 */
data class AzimuthSignals(
    @field:DecimalMin("0") @field:DecimalMax("360") var azimuth: Double,
    var signals: MutableList<@Valid Signal>
)
