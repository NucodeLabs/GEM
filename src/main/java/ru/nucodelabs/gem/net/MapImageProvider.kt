package ru.nucodelabs.gem.net

interface MapImageProvider {
    fun requestImage(mapImageRequest: MapImageRequest): MapImageResponse
}