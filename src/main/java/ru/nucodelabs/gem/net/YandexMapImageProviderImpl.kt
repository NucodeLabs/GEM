package ru.nucodelabs.gem.net

import com.google.inject.Inject
import javafx.scene.image.Image
import ru.nucodelabs.geo.anisotropy.calc.map.*
import ru.nucodelabs.geo.ves.calc.inverse.map.InverseSolver
import kotlin.math.abs

class YandexMapImageProviderImpl @Inject constructor(
    private val mapImageProvider: MapImageProvider
) : MapImageProviderPort {
    override fun requestImage(mapImageRequest: MapImageRequest): MapImageResponse {
        val offset = Offset(
            mapImageRequest.expectedDistanceFromCenterInMeters,
            mapImageRequest.expectedDistanceFromCenterInMeters
        )
        val bottomLeft = mapImageRequest.center - offset

        val upperRight = mapImageRequest.center + offset


        val stream = mapImageProvider.requestImage(
            lonBottomLeft = bottomLeft.longitudeInDegrees,
            latBottomLeft = bottomLeft.latitudeInDegrees,
            lonUpperRight = upperRight.longitudeInDegrees,
            latUpperRight = upperRight.latitudeInDegrees,
            width = 450,
            height = 450
        )
        val imageWithSquare = makeRequestSquare(
            mapImageRequest, mapImageRequest.expectedDistanceFromCenterInMeters
        )
        return MapImageResponse(
            Image(stream), getRealSize(imageWithSquare, mapImageRequest.expectedDistanceFromCenterInMeters)
        )
    }

    private fun getRealSize(imageWithSquare: Image, expectedMaxDistance: Double): Double {
        return calculateSize(imageWithSquare, expectedMaxDistance)
    }

    private fun makeRequestSquare(mapImageRequest: MapImageRequest, expectedMaxDistance: Double): Image {
        val bottomLeft = mapImageRequest.center - Offset(expectedMaxDistance, expectedMaxDistance)

        val upperRight = mapImageRequest.center + Offset(expectedMaxDistance, expectedMaxDistance)
        val upperLeft = mapImageRequest.center + Offset(
            -expectedMaxDistance,
            expectedMaxDistance
        )
        val bottomRight = mapImageRequest.center + Offset(
            expectedMaxDistance,
            -expectedMaxDistance
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

    private fun calculateSize(imageWithSquare: Image, expectedMaxDistance: Double): Double {

        val points = imageParser(imageWithSquare)
        val solver = InverseSolver(points)
        val corners = solver.getOptimizedAngles(Pair(Point(0, 0), Point(450, 450)))
        return expectedMaxDistance + pixelsToMeters(
            abs(corners.first.x - corners.second.x),
            corners.second.x,
            expectedMaxDistance
        )
    }

    private fun imageParser(image: Image): MutableList<Point> {
        val pixelReader = image.pixelReader
        val redPoints = mutableListOf<Point>()
        for (y in 0 until image.height.toInt()) {
            for (x in 0 until image.width.toInt()) {
                val pixel = pixelReader.getArgb(x, y)
                val red = pixel shr 16 and 0xff
                val green = pixel shr 8 and 0xff
                val blue = pixel and 0xff
                if (red > 200 && green < 30 && blue < 30) {
                    val point = Point(x, y)
                    redPoints.add(point)
                }
            }
        }
        return redPoints
    }

    private fun pixelsToMeters(pixelCount: Int, xCord: Int, maxDist: Double): Double {
        val extraInPixels = 450.0 - xCord.toDouble()
        val metersInPixel = (maxDist * 2.0) / pixelCount.toDouble()
        return extraInPixels * metersInPixel
    }
}