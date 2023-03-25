package ru.nucodelabs.gem.file.dto.anisotropy

data class ModelLayerDto(
    val power: Double,
    val resistance: Double,
    val isFixedPower: Boolean?,
    val isFixedResistance: Boolean?,
    var verticalAnisotropyCoefficient: Double,
    var azimuthAnisotropyCoefficient: Double,
)
