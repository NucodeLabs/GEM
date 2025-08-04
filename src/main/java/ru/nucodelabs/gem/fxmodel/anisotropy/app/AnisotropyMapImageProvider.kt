package ru.nucodelabs.gem.fxmodel.anisotropy.app

import jakarta.inject.Inject
import ru.nucodelabs.gem.fxmodel.map.MapImageData
import ru.nucodelabs.gem.net.MapImageProvider
import ru.nucodelabs.gem.net.MapImageRequest
import ru.nucodelabs.geo.anisotropy.AzimuthSignals
import ru.nucodelabs.geo.anisotropy.calc.map.MapSizer
import ru.nucodelabs.geo.anisotropy.calc.map.Wgs
import ru.nucodelabs.geo.anisotropy.calc.map.maxAb2WithAzimuth

class AnisotropyMapImageProvider @Inject constructor(
    private val mapImageProvider: MapImageProvider
) {
    /**
     * Returns satellite image
     */
    fun satImage(center: Wgs, signals: List<AzimuthSignals>, size: Int, scale: Double): MapImageData {
        val expectedMaxDistanceFromCenter: Double
        val mapSizer = MapSizer(
            center = center,
            maxAb2WithAzimuth(signals),
            scale = scale
        )
        expectedMaxDistanceFromCenter = mapSizer.maxAbsXFromCenterScaledInMeters
        val mapImageRequest = MapImageRequest(center, expectedMaxDistanceFromCenter, size)
        val mapImageResponse = mapImageProvider.requestImage(mapImageRequest)
        return MapImageData(
            mapImageResponse.image,
            -mapImageResponse.actualDistanceFromCenterInMeters,
            mapImageResponse.actualDistanceFromCenterInMeters,
            -mapImageResponse.actualDistanceFromCenterInMeters,
            mapImageResponse.actualDistanceFromCenterInMeters
        )
    }
}