package ru.nucodelabs.geo.anisotropy

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Positive
import ru.nucodelabs.geo.ves.DEFAULT_ERROR
import ru.nucodelabs.geo.ves.calc.rhoA

data class Signal(
    @field:Positive val ab2: Double,
    @field:Positive val mn2: Double,
    @field:Min(0) val amperage: Double,
    @field:Min(0) val voltage: Double,
    @field:DecimalMin("0.1") val resistanceApparent: Double = rhoA(ab2, mn2, amperage, voltage),
    @field:Min(0) @field:Max(100) val errorResistanceApparent: Double = DEFAULT_ERROR,
    val isHidden: Boolean = false
)
