package ru.nucodelabs.gem.net

class YandexMapsImageProvider : MapImageProvider {
    override fun requestImage(
        lonBottomLeft: Double,
        latBottomLeft: Double,
        lonUpperRight: Double,
        latUpperRight: Double
    ): String {
        return "https://static-maps.yandex.ru/1.x/" +
                "?l=sat" +
                "&bbox=${lonBottomLeft},${latBottomLeft}~${lonUpperRight},${latUpperRight}"
    }

    override fun requestImage(
        lonBottomLeft: Double,
        latBottomLeft: Double,
        lonUpperRight: Double,
        latUpperRight: Double,
        width: Int,
        height: Int
    ): String {
        return "https://static-maps.yandex.ru/1.x/" +
                "?l=sat" +
                "&bbox=${lonBottomLeft},${latBottomLeft}~${lonUpperRight},${latUpperRight}" +
                "&size=${width},${height}"
    }

}