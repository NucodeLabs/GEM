package ru.nucodelabs.gem.net

import java.io.InputStream
import java.net.URL
import java.net.UnknownServiceException

class YandexMapsImageProvider : MapImageProvider {

    override fun requestImage(
        lonBottomLeft: Double,
        latBottomLeft: Double,
        lonUpperRight: Double,
        latUpperRight: Double
    ): InputStream {
        return makeRequest(
            "https://static-maps.yandex.ru/1.x/" +
                    "?l=sat" +
                    "&bbox=${lonBottomLeft},${latBottomLeft}~${lonUpperRight},${latUpperRight}"
        )
    }

    override fun requestImage(
        lonBottomLeft: Double,
        latBottomLeft: Double,
        lonUpperRight: Double,
        latUpperRight: Double,
        width: Int,
        height: Int
    ): InputStream {
        return makeRequest(
            "https://static-maps.yandex.ru/1.x/" +
                    "?l=sat" +
                    "&bbox=${lonBottomLeft},${latBottomLeft}~${lonUpperRight},${latUpperRight}" +
                    "&size=${width},${height}"
        )
    }

    private fun makeRequest(urlString: String): InputStream {
        val url = URL(urlString)
        val con = url.openConnection()
        if (con.contentType != "image/jpeg") {
            throw UnknownServiceException(url.readText())
        } else {
            return url.openStream()
        }
    }
}