package ru.nucodelabs.gem.file.dto.anisotropy

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ModelLayerDto(
    val power: FixableDoubleValueDto,
    val resistance: FixableDoubleValueDto,
    var verticalAnisotropyCoefficient: FixableDoubleValueDto,
    var azimuth: Double,
    var azimuthAnisotropyCoefficient: FixableDoubleValueDto,
)
