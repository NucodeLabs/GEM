package ru.nucodelabs.geo.ves.calc

import ru.nucodelabs.geo.ves.ExperimentalData
import ru.nucodelabs.geo.ves.ReadOnlyExperimentalSignal

fun <T : ReadOnlyExperimentalSignal> orderByDistances(): Comparator<T> = compareBy<T> { it.ab2 }.thenBy { it.mn2 }

fun ExperimentalData.withCalculatedResistivityApparent() =
    this.copy(resistivityApparent = rhoA(ab2, mn2, amperage, voltage))

val ReadOnlyExperimentalSignal.resistivityApparentUpperBoundByError
    get() = (resistivityApparent + resistivityApparent * errorResistivityApparent / 100).coerceAtLeast(1.0)

val ReadOnlyExperimentalSignal.resistivityApparentLowerBoundByError
    get() = (resistivityApparent - resistivityApparent * errorResistivityApparent / 100).coerceAtLeast(1.0)