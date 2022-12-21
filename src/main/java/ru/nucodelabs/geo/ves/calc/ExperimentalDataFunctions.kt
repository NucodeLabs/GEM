package ru.nucodelabs.geo.ves.calc

import ru.nucodelabs.geo.ves.ExperimentalData

import ru.nucodelabs.mathves.Normalization

fun orderByDistances() =
    compareBy<ExperimentalData> { it.ab2 }
        .thenBy { it.mn2 }

fun ExperimentalData.withCalculatedResistanceApparent() =
    this.copy(resistanceApparent = rhoA(ab2, mn2, amperage, voltage))

val ExperimentalData.resistanceApparentUpperBoundByError
    get() = (resistanceApparent + resistanceApparent * errorResistanceApparent / 100).coerceAtLeast(1.0)

val ExperimentalData.resistanceApparentLowerBoundByError
    get() = (resistanceApparent - resistanceApparent * errorResistanceApparent / 100).coerceAtLeast(1.0)