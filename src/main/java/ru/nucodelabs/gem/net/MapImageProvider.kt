package ru.nucodelabs.gem.net

import java.io.InputStream
import java.net.UnknownServiceException

interface MapImageProvider {
    /**
     * Returns image URL
     * @param lonBottomLeft longitude in degrees of bottom left corner
     * @param latBottomLeft latitude in degrees of bottom left corner
     * @param lonUpperRight longitude in degrees of upper right corner
     * @param latUpperRight latitude in degrees of upper right corner
     * @throws WrongResponseException if response is not image
     */
    fun requestImage(
        lonBottomLeft: Double,
        latBottomLeft: Double,
        lonUpperRight: Double,
        latUpperRight: Double
    ): InputStream

    /**
     * Returns image URL
     * @param lonBottomLeft longitude in degrees of bottom left corner
     * @param latBottomLeft latitude in degrees of bottom left corner
     * @param lonUpperRight longitude in degrees of upper right corner
     * @param latUpperRight latitude in degrees of upper right corner
     * @param width width of image in pixels
     * @param height height of image in pixels
     * @throws WrongResponseException if response is not image
     */
    fun requestImage(
        lonBottomLeft: Double,
        latBottomLeft: Double,
        lonUpperRight: Double,
        latUpperRight: Double,
        width: Int,
        height: Int
    ): InputStream
}