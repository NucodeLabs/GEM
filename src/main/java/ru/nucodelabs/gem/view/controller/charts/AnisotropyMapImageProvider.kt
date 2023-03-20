package ru.nucodelabs.gem.view.controller.charts

import javafx.scene.image.Image
import javafx.scene.paint.Color
import ru.nucodelabs.gem.net.MapImageProvider
import ru.nucodelabs.geo.map.MapSizer
import ru.nucodelabs.geo.map.Offset
import ru.nucodelabs.geo.map.plus
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
        val upperLeft = mapSizer.center + Offset(
            -mapSizer.maxAbsXFromCenterInMeters,
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
        val extraDist = imageParser(Image(imageWithLine), mapSizer.maxAbsXFromCenterInMeters)
        val stream = mapImageProvider.requestImage(
            lonBottomLeft = bottomLeft.longitudeInDegrees,
            latBottomLeft = bottomLeft.latitudeInDegrees,
            lonUpperRight = upperRight.longitudeInDegrees,
            latUpperRight = upperRight.latitudeInDegrees,
            width = 450,
            height = 450
        )
        println("---Extra distance: $extraDist")
        return Image(stream)
    }

    private fun imageParser(image: Image, maxDist: Double): Double {
        val pixelReader = image.pixelReader
        var pixelCount = 0
        var endOfLine = false
        var startOfLine = false
        val threshold = 0.1
        val sobelX = arrayOf(intArrayOf(-1,0,1), intArrayOf(-2,0,2), intArrayOf(-1,0,1))
        val sobelY = arrayOf(intArrayOf(-1, -2, -1), intArrayOf(0, 0, 0), intArrayOf(1, 2, 1))

        var startX = Int.MAX_VALUE
        var startY = Int.MAX_VALUE
        var endX = Int.MIN_VALUE
        var endY = Int.MIN_VALUE
        for (y in 1 until image.height.toInt() - 1) {
            for (x in 1 until image.width.toInt() - 1) {
                val pixel = pixelReader.getArgb(x, y)
                val red = pixel shr 16 and 0xff
                val green = pixel shr 8 and 0xff
                val blue = pixel and 0xff
                if (/*!endOfLine && */red > 200 && green < 30 && blue < 30) {
                    var pixelGradient = 0.0
                    for (i in -1..1) {
                        for (j in -1..1) {
                            val nPixel = pixelReader.getArgb(x + i, y + j)
                            val nRed = nPixel shr 16 and 0xff
                            val nGreen = nPixel shr 8 and 0xff
                            val nBlue = nPixel and 0xff
                            val nIntensity = (nRed + nGreen + nBlue) / 3.0
                            pixelGradient += sobelX[i + 1][j + 1] * nIntensity
                            pixelGradient += sobelY[i + 1][j + 1] * nIntensity
                        }
                    }
                    if (pixelGradient > threshold) {
                        if (x < startX) startX = x
                        if (x > endX) endX = x
                        if (y < startY) startY = y
                        if (y > endY) endY = y
                        pixelCount = endX - startX
                        //startOfLine = true
                        //continue
                    }
                } /*else if (startOfLine && !endOfLine) {
                    endOfLine = true
                    endX = x
                }*/
            }
            //if (endOfLine) break
        }
        return pixelsToMeters(pixelCount, endX, maxDist)
    }

    private fun pixelsToMeters(pixelCount: Int, xCord: Int, maxDist: Double): Double {
        val extraInPixels = 450.0 - xCord.toDouble()
        println("---End of line: $xCord")
        println("---Line length in pixels: $pixelCount")
        println("---Extra length in pixels: $extraInPixels")
        val metersInPixel = (maxDist * 2.0) / pixelCount.toDouble()
        return extraInPixels * metersInPixel
    }
}