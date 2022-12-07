package ru.nucodelabs.geo.ves

fun ModelLayer.toTabulatedRow() = "$power $resistance"

@JvmName("convertModelDataToTabulatedTable")
fun List<ModelLayer>.toTabulatedTable() = joinToString(separator = "\n") { it.toTabulatedRow() }

fun ExperimentalData.toTabulatedRow() = "$ab2 $mn2 $voltage $amperage $resistanceApparent"

@JvmName("convertExperimentalDataToTabulatedTable")
fun List<ExperimentalData>.toTabulatedTable() = joinToString(separator = "\n") { it.toTabulatedRow() }