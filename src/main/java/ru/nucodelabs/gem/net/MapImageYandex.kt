package ru.nucodelabs.gem.net

import javafx.scene.image.Image
import ru.nucodelabs.geo.anisotropy.calc.map.Point
import ru.nucodelabs.geo.ves.calc.inverse.map.InverseSolver
import kotlin.math.abs

class MapImageYandex (
    img: Image,
    imgSqr: Image,
    expectedDistance: Double
) : MapImage {

    private val image: Image
    private val imageWithSquare: Image
    private val expectedDistance: Double
    private val actualDistance: Double
    init {
        this.image = img
        this.imageWithSquare = imgSqr
        this.expectedDistance = expectedDistance
        this.actualDistance = getRealSize(this.imageWithSquare)
    }
    override fun getImage(): Image {
        return image
    }

    override fun getDistFromCenter(): Double {
        return actualDistance
    }
    private fun getRealSize(imageWithSquare: Image): Double {

        val points = imageParser(imageWithSquare)
        val solver = InverseSolver(points)
        val corners = solver.getOptimizedAngles(Pair(Point(0, 0), Point(450, 450)))
        val expected = this.expectedDistance
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
