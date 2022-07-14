package ru.nucodelabs.data.ves

fun orderByDistances() =
    compareBy<ExperimentalData> { it.ab2 }
        .thenBy { it.mn2 }

fun ExperimentalData.withCalculatedResistanceApparent() =
    this.copy(resistanceApparent = rhoA(ab2, mn2, amperage, voltage))