package ru.nucodelabs.geo.anisotropy

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min

/**
 * Слой модели
 * @property power Мощность, м
 * @property resistance Сопротивление, Ом * м
 * @property isFixedPower Значение зафиксировано для обратной задачи
 * @property isFixedResistance Значение зафиксировано для обратной задачи
 * @property verticalAnisotropyCoefficient коэффициент анизотропии по оси Z
 * @property azimuthAnisotropyCoefficient коэффициент анизотропии в азимутальной плоскости XY
 */
data class ModelLayer(
    @field:Min(0) var power: Double,
    @field:DecimalMin("0.1") var resistance: Double,
    var isFixedPower: Boolean = false,
    var isFixedResistance: Boolean = false,
    var verticalAnisotropyCoefficient: Double,
    var azimuthAnisotropyCoefficient: Double,
)