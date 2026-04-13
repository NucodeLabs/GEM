package ru.nucodelabs.geo.ves

interface ModelDataSet {
    val modelData: List<ReadOnlyModelLayer>
    val modelZ: Double
}
