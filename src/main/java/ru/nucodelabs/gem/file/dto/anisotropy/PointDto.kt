package ru.nucodelabs.gem.file.dto.anisotropy

data class PointDto(
    val azimuthSignals: List<AzimuthSignalsDto>,
    val model: List<ModelLayerDto>?
)
