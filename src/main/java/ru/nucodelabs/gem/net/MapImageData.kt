package ru.nucodelabs.gem.net

import javafx.scene.image.Image


data class MapImageData (
    val image: Image,
    val xLowerBound: Double,
    val xUpperBound: Double,
    val yLowerBound: Double,
    val yUpperBound: Double
)