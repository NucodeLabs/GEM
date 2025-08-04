package ru.nucodelabs.gem.file.dto.anisotropy

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AzimuthSignalsDto(
    val azimuth: Double,
    val signals: List<SignalDto>?
)
