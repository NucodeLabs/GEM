package ru.nucodelabs.data.ves

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Positive

/**
 * Экспериментальное измерение
 * @property ab2 AB/2, м
 * @property mn2 MN/2, м
 * @property amperage Ток, мА
 * @property voltage Напряжение, мВ
 * @property resistanceApparent Сопротивление кажущееся, Ом * м
 * @property errorResistanceApparent Погрешность, %
 * @property isHidden Отключена для интерпретации
 */
data class ExperimentalData(
    @field:Positive val ab2: Double,
    @field:Positive val mn2: Double,
    @field:Min(0) val amperage: Double,
    @field:Min(0) val voltage: Double,
    @field:Min(1) val resistanceApparent: Double = resistanceApparent(ab2, mn2, amperage, voltage),
    @field:Min(0) @Max(100) val errorResistanceApparent: Double = 5.0,
    val isHidden: Boolean = false
)
