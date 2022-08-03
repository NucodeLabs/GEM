package ru.nucodelabs.data.ves

import ru.nucodelabs.mathves.ModelFunctions

fun ModelLayer.divide(): Pair<ModelLayer, ModelLayer> = copy(power = power / 2) to copy(power = power / 2)

fun List<ModelLayer>.join(): ModelLayer =
    ModelFunctions.joinLayers(
        map { it.power }.toDoubleArray(),
        map { it.resistance }.toDoubleArray()
    ).let {
        ModelLayer(power = it[0], resistance = it[1])
    }

fun joinLayers(layers: List<ModelLayer>): ModelLayer = layers.join()