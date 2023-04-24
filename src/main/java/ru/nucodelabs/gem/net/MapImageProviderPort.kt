package ru.nucodelabs.gem.net

interface MapImageProviderPort {
    fun requestImage(mapImageRequest: MapImageRequest): MapImageResponse
}