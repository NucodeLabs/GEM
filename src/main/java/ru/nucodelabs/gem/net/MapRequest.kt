package ru.nucodelabs.gem.net

import ru.nucodelabs.geo.anisotropy.calc.map.MapSizer

interface MapRequest {
    fun makeRequest(mapSizer: MapSizer): MapImage
}