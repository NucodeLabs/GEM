package ru.nucodelabs.geo.anisotropy

import jakarta.validation.Valid
import jakarta.validation.constraints.Size

/**
 * @property azimuthSignals экспериментальные данные (сигналы)
 * @property model данные модели
 */
data class Point(
    var azimuthSignals: MutableList<@Valid AzimuthSignals> = mutableListOf(),
    @field:Size(max = 40) var model: MutableList<@Valid ModelLayer> = mutableListOf(),
    var z: Double = DEFAULT_POINT_Z,
    var comment: String = DEFAULT_POINT_COMMENT
)
