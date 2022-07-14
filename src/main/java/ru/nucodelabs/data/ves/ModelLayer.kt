package ru.nucodelabs.data.ves

import jakarta.validation.constraints.Min

/**
 * Слой модели
 * @property power Мощность, м
 * @property resistance Сопротивление, Ом * м
 * @property isFixedPower Значение зафиксировано для обратной задачи
 * @property isFixedResistance Значение зафиксировано для обратной задачи
 */
data class ModelLayer(
    @field:Min(0) val power: Double,
    @field:Min(1) val resistance: Double,
    val isFixedPower: Boolean = false,
    val isFixedResistance: Boolean = false
)
