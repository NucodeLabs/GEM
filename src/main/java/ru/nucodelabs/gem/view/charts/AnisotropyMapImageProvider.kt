package ru.nucodelabs.gem.view.charts

import javafx.scene.image.Image
import ru.nucodelabs.gem.net.MapImageProvider
import ru.nucodelabs.geo.map.AzimuthPoint
import ru.nucodelabs.geo.map.MapSizer
import ru.nucodelabs.geo.map.Wsg
import javax.inject.Inject

class AnisotropyMapImageProvider @Inject constructor(
    private val mapImageProvider: MapImageProvider
) {
    /**
     * Returns satellite image
     * @throws WrongResponseException if API call response is not image
     */
    fun satImage(center: Wsg, maxAb2WithAzimuth: Collection<AzimuthPoint>, scale: Double = 0.8): Image {
        val mapSizer = MapSizer(center, maxAb2WithAzimuth, scale)
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