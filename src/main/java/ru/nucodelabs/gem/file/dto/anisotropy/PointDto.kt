package ru.nucodelabs.gem.file.dto.anisotropy

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PointDto(
    val azimuthSignals: List<AzimuthSignalsDto>,
    val model: List<ModelLayerDto>?,
    val z: Double?,
    val comment: String?
)
