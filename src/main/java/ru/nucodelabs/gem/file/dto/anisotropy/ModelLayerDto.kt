package ru.nucodelabs.gem.file.dto.anisotropy

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ModelLayerDto(
    val power: Double,
    val resistance: Double,
    val isFixedPower: Boolean?,
    val isFixedResistance: Boolean?,
    var verticalAnisotropyCoefficient: Double,
    var azimuth: Double,
    var azimuthAnisotropyCoefficient: Double,
)
