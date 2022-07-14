package ru.nucodelabs.data.ves

fun orderByDistances() =
    compareBy<ExperimentalData> { it.ab2 }
        .thenBy { it.mn2 }

fun ExperimentalData.withCalculatedResistanceApparent() =
    this.copy(resistanceApparent = resistanceApparent(ab2, mn2, amperage, voltage))

//fun OldExperimentalData.toKt() =
//    ExperimentalData(ab2, mn2, resistanceApparent, errorResistanceApparent, amperage, voltage)
//
//fun ExperimentalData.toOld() =
//    OldExperimentalData.create(ab2, mn2, resistanceApparent, errorResistanceApparent, amperage, voltage)