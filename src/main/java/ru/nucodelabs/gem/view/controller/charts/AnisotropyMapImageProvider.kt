package ru.nucodelabs.gem.view.controller.charts

import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import ru.nucodelabs.gem.net.MapImageProvider
import ru.nucodelabs.geo.map.*
import javax.inject.Inject
import kotlin.math.abs

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
        val upperLeft = mapSizer.center + Offset(
            -1 * mapSizer.maxAbsXFromCenterInMeters,
            mapSizer.maxAbsYFromCenterInMeters
            )
        val imageWithLine = mapImageProvider.requestImage(
            lonBottomLeft = bottomLeft.longitudeInDegrees,
            latBottomLeft = bottomLeft.latitudeInDegrees,
            lonUpperRight = upperRight.longitudeInDegrees,
            latUpperRight = upperRight.latitudeInDegrees,
            lonUpperLeft = upperLeft.longitudeInDegrees,
            latUpperLeft = upperLeft.latitudeInDegrees,
            width = 450,
            height = 450
        )
        //val extraDist = imageParser(Image(imageWithLine), mapSizer.maxAbsXFromCenterInMeters)
/*        val stream = mapImageProvider.requestImage(
            lonBottomLeft = bottomLeft.longitudeInDegrees,
            latBottomLeft = bottomLeft.latitudeInDegrees,
            lonUpperRight = upperRight.longitudeInDegrees,
            latUpperRight = upperRight.latitudeInDegrees,
            width = 450,
            height = 450
        )*/
        return Image(imageWithLine)
    }

    private fun imageParser(image: Image, maxDist: Double): Double {
        val pixelReader = image.pixelReader
        var pixelCount = 0
        var endX = 0
        var endOfLine = false
        for (y in 0 until image.height.toInt()) {
            for (x in 0 until image.width.toInt()) {
                while (Color.web(pixelReader.getArgb(x, y).toString()) == Color.RED) {
                    pixelCount++
                    endOfLine = true
                }
                if (endOfLine) {
                    endX = x
                }
            }
            if (endOfLine) break
        }
        return pixelsToMeters(pixelCount, endX, maxDist)
    }

    private fun pixelsToMeters(pixelCount: Int, xCord: Int, maxDist: Double): Double {
        val extraInPixels = 450 - xCord
        val metersInPixel = pixelCount.toDouble() / (maxDist * 2.0)
        return extraInPixels * metersInPixel
    }
}