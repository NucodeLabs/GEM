package ru.nucodelabs.geo.anisotropy

import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import ru.nucodelabs.geo.ves.DEFAULT_PICKET_COMMENT
import ru.nucodelabs.geo.ves.DEFAULT_PICKET_Z

/**
 * @property azimuthSignals экспериментальные данные (сигналы)
 * @property model данные модели
 */
data class Point(
    var azimuthSignals: MutableList<@Valid AzimuthSignals> = mutableListOf(),
    @field:Size(max = 40) var model: MutableList<@Valid ModelLayer> = mutableListOf(),
    var z: Double = DEFAULT_PICKET_Z,
    var comment: String = DEFAULT_PICKET_COMMENT
)
