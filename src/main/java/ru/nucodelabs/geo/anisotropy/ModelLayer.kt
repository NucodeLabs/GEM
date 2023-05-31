package ru.nucodelabs.geo.anisotropy

import jakarta.validation.constraints.DecimalMin

/**
 * Слой модели
 * @property power Мощность, м
 * @property resistance Сопротивление, Ом * м
 * @property isFixedPower Значение зафиксировано для обратной задачи
 * @property isFixedResistance Значение зафиксировано для обратной задачи
 * @property verticalAnisotropyCoefficient коэффициент анизотропии по оси Z
 * @property azimuth азимутальный угол анизотропии
 * @property azimuthAnisotropyCoefficient коэффициент анизотропии в азимутальной плоскости XY
 */
data class ModelLayer(
    @field:DecimalMin(MIN_POWER.toString()) var power: Double,
    @field:DecimalMin(MIN_RESISTANCE.toString()) var resistance: Double,
    var isFixedPower: Boolean = false,
    var isFixedResistance: Boolean = false,
    var verticalAnisotropyCoefficient: Double,
    var azimuth: Double,
    var azimuthAnisotropyCoefficient: Double,
)