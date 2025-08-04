package ru.nucodelabs.geo.anisotropy

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Positive
import ru.nucodelabs.geo.ves.calc.k
import ru.nucodelabs.geo.ves.calc.rhoA
import ru.nucodelabs.geo.ves.calc.u

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
data class Signal(
    @field:Positive val ab2: Double,
    @field:Positive val mn2: Double,
    @field:Min(0) var amperage: Double,
    @field:Min(0) var voltage: Double,
    @field:DecimalMin(MIN_RESISTANCE.toString()) var resistanceApparent: Double = rhoA(ab2, mn2, amperage, voltage),
    @field:Min(0) @field:Max(100) var errorResistanceApparent: Double = DEFAULT_ERROR,
    val isHidden: Boolean = false
)

fun signalWithOnlyRhoA(
    ab2: Double,
    mn2: Double,
    resistanceApparent: Double,
    amperage: Double = DEFAULT_AMPERAGE,
    voltage: Double = u(resistanceApparent, amperage, k(ab2, mn2)),
    errorResistanceApparent: Double = DEFAULT_ERROR,
    isHidden: Boolean = false
) = Signal(
    ab2 = ab2,
    mn2 = mn2,
    amperage = amperage,
    voltage = voltage,
    resistanceApparent = resistanceApparent,
    errorResistanceApparent = errorResistanceApparent,
    isHidden = isHidden
)
