package ru.nucodelabs.geo.anisotropy

/**
 * Слой модели
 * @property power Мощность, м
 * @property resistance Сопротивление, Ом * м
 * @property verticalAnisotropyCoefficient коэффициент анизотропии по оси Z
 * @property azimuthAnisotropyCoefficient коэффициент анизотропии в азимутальной плоскости XY
 */
data class ModelLayer(
    var power: FixableValue<Double>,
    var resistance: FixableValue<Double>,
    var verticalAnisotropyCoefficient: FixableValue<Double>,
    var azimuthAnisotropyCoefficient: FixableValue<Double>,
)