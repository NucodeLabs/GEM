package ru.nucodelabs.data.ves

fun ModelLayer.divide(): Pair<ModelLayer, ModelLayer> = copy(power = power / 2) to copy(power = power / 2)