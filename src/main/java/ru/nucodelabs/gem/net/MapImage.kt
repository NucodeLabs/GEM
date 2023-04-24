package ru.nucodelabs.gem.net

import javafx.scene.image.Image

interface MapImage {

    fun getImage(): Image?

    fun getDistFromCenter(): Double
}
