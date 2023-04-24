package ru.nucodelabs.gem.net

import javafx.scene.image.Image

data class MapImageResponse(
    val image: Image,
    val actualDistanceFromCenterInMeters: Double
)
