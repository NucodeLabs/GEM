package ru.nucodelabs.geo.ves.calc

import ru.nucodelabs.geo.ves.ModelLayer
import ru.nucodelabs.geo.ves.ReadOnlyModelLayer
import ru.nucodelabs.mathves.ModelFunctions

fun ModelLayer.divide(): Pair<ModelLayer, ModelLayer> = copy(power = power / 2) to copy(power = power / 2)

fun List<ReadOnlyModelLayer>.join(): ModelLayer =
    ModelFunctions.joinLayers(
        map { it.power }.toDoubleArray(),
        map { it.resistivity }.toDoubleArray()
    ).let {
        ModelLayer(power = it[0], resistivity = it[1])
    }