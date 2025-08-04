package ru.nucodelabs.geo.anisotropy

import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import ru.nucodelabs.geo.anisotropy.calc.map.Wgs

/**
 * @property azimuthSignals экспериментальные данные (сигналы)
 * @property model данные модели
 */
data class Point(
    var center: Wgs? = null,
    var azimuthSignals: MutableList<@Valid AzimuthSignals> = mutableListOf(),
    @field:Size(max = MAX_LAYERS_COUNT) var model: MutableList<@Valid ModelLayer> = mutableListOf(),
    var z: Double = DEFAULT_POINT_Z,
    var comment: String = DEFAULT_POINT_COMMENT
)
