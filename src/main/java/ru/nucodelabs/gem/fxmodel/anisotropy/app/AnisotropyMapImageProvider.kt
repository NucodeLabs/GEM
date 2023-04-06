package ru.nucodelabs.gem.fxmodel.anisotropy.app

import javafx.scene.image.Image
import ru.nucodelabs.gem.net.MapImageProvider
import ru.nucodelabs.geo.ves.calc.inverse.map.InverseSolver
import ru.nucodelabs.geo.anisotropy.calc.map.MapSizer
import ru.nucodelabs.geo.anisotropy.calc.map.Offset
import ru.nucodelabs.geo.anisotropy.calc.map.Point
import ru.nucodelabs.geo.anisotropy.calc.map.plus
import javax.inject.Inject
import kotlin.math.abs

class AnisotropyMapImageProvider @Inject constructor(
    private val mapImageProvider: MapImageProvider
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
            latUpperRight = upperRight.latitudeInDegrees,
            width = 450,
            height = 450
        )
        return Image(stream)
    }

    fun getRealSize(mapSizer: MapSizer): Double {
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

        val points = imageParser(Image(imageWithSquare))
        val solver = InverseSolver(points)
        val corners = solver.getOptimizedAngles(Pair(Point(0, 0), Point(450, 450)))
        val expected = mapSizer.maxAbsYFromCenterInMeters
        return expected + pixelsToMeters(abs(corners.first.x - corners.second.x), corners.second.x, expected)
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