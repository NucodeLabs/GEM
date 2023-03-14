package ru.nucodelabs.gem.view.controller.charts

import javafx.scene.image.Image
import ru.nucodelabs.gem.net.MapImageProvider
import ru.nucodelabs.geo.map.MapSizer
import javax.inject.Inject

class AnisotropyMapImageProvider @Inject constructor(
    val mapImageProvider: MapImageProvider
) {
    /**
     * Returns satellite image
     * @throws ru.nucodelabs.gem.net.WrongResponseException if API call response is not image
     */
    fun satImage(mapSizer: MapSizer): Image {
        val bottomLeft = mapSizer.bottomLeftCorner
        val upperRight = mapSizer.upperRightCorner
        val stream = mapImageProvider.requestImage(
            lonBottomLeft = bottomLeft.longitudeInDegrees,
            latBottomLeft = bottomLeft.latitudeInDegrees,
            lonUpperRight = upperRight.longitudeInDegrees,
            latUpperRight = upperRight.latitudeInDegrees
        )
        return Image(stream)
    }
}