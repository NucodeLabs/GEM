package ru.nucodelabs.geo.anisotropy

import jakarta.validation.Valid

/**
 * @property azimuthSignals экспериментальные данные (сигналы)
 * @property model данные модели
 */
data class Point(
    var azimuthSignals: MutableList<@Valid AzimuthSignals> = mutableListOf(),
    var model: MutableList<@Valid ModelLayer> = mutableListOf()
)
