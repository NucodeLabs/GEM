package ru.nucodelabs.gem.fxmodel.map

import javafx.scene.image.Image


data class MapImageData (
    val image: Image,
    val xLowerBound: Double,
    val xUpperBound: Double,
    val yLowerBound: Double,
    val yUpperBound: Double
)