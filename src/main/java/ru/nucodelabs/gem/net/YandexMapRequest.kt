package ru.nucodelabs.gem.net

import com.google.inject.Inject
import javafx.scene.image.Image
import ru.nucodelabs.geo.anisotropy.calc.map.MapSizer
import ru.nucodelabs.geo.anisotropy.calc.map.Offset
import ru.nucodelabs.geo.anisotropy.calc.map.plus

class YandexMapRequest @Inject constructor(
    private val mapImageProvider: MapImageProvider
) : MapRequest {
    override fun makeRequest(mapSizer: MapSizer): MapImage {
        val bottomLeft = mapSizer.bottomLeftCorner
        val upperRight = mapSizer.upperRightCorner

        val stream = mapImageProvider.requestImage(
            lonBottomLeft = bottomLeft.longitudeInDegrees,
            latBottomLeft = bottomLeft.latitudeInDegrees,
            lonUpperRight = upperRight.longitudeInDegrees,
            latUpperRight = upperRight.latitudeInDegrees,
            width = 450,
            height = 450
        )

        return MapImageYandex(Image(stream), makeRequestSquare(mapSizer), mapSizer.maxAbsXFromCenterInMeters)
    }

    private fun makeRequestSquare(mapSizer: MapSizer): Image {
        val bottomLeft = mapSizer.bottomLeftCorner
        val upperRight = mapSizer.upperRightCorner
        val upperLeft = mapSizer.center + Offset(
            -mapSizer.maxAbsXFromCenterInMeters,
            mapSizer.maxAbsYFromCenterInMeters
        )
        val bottomRight = mapSizer.center + Offset(
            mapSizer.maxAbsXFromCenterInMeters,
            -mapSizer.maxAbsYFromCenterScaledInMeters
        )

        val imageWithSquare = mapImageProvider.requestImage(
            lonBottomLeft = bottomLeft.longitudeInDegrees,
            latBottomLeft = bottomLeft.latitudeInDegrees,
            lonUpperRight = upperRight.longitudeInDegrees,
            latUpperRight = upperRight.latitudeInDegrees,
            lonUpperLeft = upperLeft.longitudeInDegrees,
            latUpperLeft = upperLeft.latitudeInDegrees,
            lonBottomRight = bottomRight.longitudeInDegrees,
            latBottomRight = bottomRight.latitudeInDegrees,
            width = 450,
            height = 450
        )
        return Image(imageWithSquare)
    }
}