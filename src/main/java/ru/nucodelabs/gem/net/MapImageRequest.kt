package ru.nucodelabs.gem.net

import ru.nucodelabs.geo.anisotropy.calc.map.Wgs

data class MapImageRequest(
    val center: Wgs,
    val expectedDistanceFromCenterInMeters: Double,
    val size: Int,
)
