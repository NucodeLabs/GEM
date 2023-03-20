package ru.nucodelabs.geo.anisotropy

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Positive
import ru.nucodelabs.geo.ves.DEFAULT_ERROR
import ru.nucodelabs.geo.ves.calc.rhoA

data class Signal(
    @field:Positive var ab2: Double,
    @field:Positive var mn2: Double,
    @field:Min(0) var amperage: Double,
    @field:Min(0) var voltage: Double,
    @field:DecimalMin("0.1") var resistanceApparent: Double = rhoA(ab2, mn2, amperage, voltage),
    @field:Min(0) @field:Max(100) var errorResistanceApparent: Double = DEFAULT_ERROR,
    var isHidden: Boolean = false
)
