package ru.nucodelabs.gem.net

interface MapImageProvider {
    /**
     * Returns image URL
     * @param lonBottomLeft longitude in degrees of bottom left corner
     * @param latBottomLeft latitude in degrees of bottom left corner
     * @param lonUpperRight longitude in degrees of upper right corner
     * @param latUpperRight latitude in degrees of upper right corner
     */
    fun requestImage(
        lonBottomLeft: Double,
        latBottomLeft: Double,
        lonUpperRight: Double,
        latUpperRight: Double
    ): String

    /**
     * Returns image URL
     * @param lonBottomLeft longitude in degrees of bottom left corner
     * @param latBottomLeft latitude in degrees of bottom left corner
     * @param lonUpperRight longitude in degrees of upper right corner
     * @param latUpperRight latitude in degrees of upper right corner
     * @param width width of image in pixels
     * @param height height of image in pixels
     */
    fun requestImage(
        lonBottomLeft: Double,
        latBottomLeft: Double,
        lonUpperRight: Double,
        latUpperRight: Double,
        width: Int,
        height: Int
    ): String
}