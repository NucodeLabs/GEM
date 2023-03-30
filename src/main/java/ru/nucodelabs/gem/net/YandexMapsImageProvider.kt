package ru.nucodelabs.gem.net

import java.io.InputStream
import java.net.URL

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

    override fun requestImage(
        lonBottomLeft: Double,
        latBottomLeft: Double,
        lonUpperRight: Double,
        latUpperRight: Double,
        lonUpperLeft: Double,
        latUpperLeft: Double,
        lonBottomRight: Double,
        latBottomRight: Double,
        width: Int,
        height: Int
    ): InputStream {
        return makeRequest(
            "https://static-maps.yandex.ru/1.x/" +
                    "?l=sat" +
                    "&bbox=${lonBottomLeft},${latBottomLeft}~${lonUpperRight},${latUpperRight}" +
                    "&size=${width},${height}" +
                    "&pl=c:FF0000FF,${lonUpperLeft},${latUpperLeft},${lonUpperRight},${latUpperRight}," +
                    "$lonUpperRight,$latUpperRight,$lonBottomRight,$latBottomRight," +
                    "$lonBottomRight,$latBottomRight,$lonBottomLeft,$latBottomLeft," +
                    "$lonBottomLeft,$latBottomLeft,$lonUpperLeft,$latUpperRight"
        )
    }

    private fun makeRequest(urlString: String): InputStream {
        val url = URL(urlString)
        val con = url.openConnection()
        if (con.contentType != "image/jpeg") {
            throw WrongResponseException(url.readText())
        } else {
            return url.openStream()
        }
    }
}