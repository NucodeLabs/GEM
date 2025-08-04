package ru.nucodelabs.gem.file.dto.anisotropy

import com.fasterxml.jackson.annotation.JsonInclude
import ru.nucodelabs.gem.file.dto.map.WgsDto

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PointDto(
    val center: WgsDto?,
    val azimuthSignals: List<AzimuthSignalsDto>,
    val model: List<ModelLayerDto>?,
    val z: Double?,
    val comment: String?
)
