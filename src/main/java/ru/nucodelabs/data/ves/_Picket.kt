package ru.nucodelabs.data.ves

fun Picket.zOfModelLayers(): List<Double> {
    val heightList: MutableList<Double> = mutableListOf()
    val power = modelData.map { it.power }
    var sum = z
    for (p in power) {
        sum -= p
        heightList.add(sum)
    }
    return heightList    // последняя уходит в бесконечность
}
