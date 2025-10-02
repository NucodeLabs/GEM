package ru.nucodelabs.geo.ves.calc.interpolation

import ru.nucodelabs.ShiraPicket
import ru.nucodelabs.geo.anisotropy.calc.map.xFromCenter
import ru.nucodelabs.geo.anisotropy.calc.map.yFromCenter
import ru.nucodelabs.util.Point

object AnisotropyTestData {
    val points: List<Point>

    init {
        val picket = ShiraPicket.picket
        val expData = picket.sortedExperimentalData
        val points1 = expData.map { e -> Point(100.0, e.ab2, e.resistivityApparent) }
        var angle = 60.0
        val points2 = points1.map { e ->
            Point(
                (xFromCenter(e.y, angle) + e.x),
                yFromCenter(e.y, angle),
                e.z
            )
        }
        angle = 120.0
        val points3 = points1.map { e ->
            Point(
                (xFromCenter(e.y, angle) + e.x),
                yFromCenter(e.y, angle),
                e.z
            )
        }
        angle = 180.0
        val points4 = points1.map { e ->
            Point(
                (xFromCenter(e.y, angle) + e.x),
                yFromCenter(e.y, angle),
                e.z
            )
        }
        angle = -60.0
        val points5 = points1.map { e ->
            Point(
                (xFromCenter(e.y, angle) + e.x),
                yFromCenter(e.y, angle),
                e.z
            )
        }
        angle = -120.0
        val points6 = points1.map { e ->
            Point(
                (xFromCenter(e.y, angle) + e.x),
                yFromCenter(e.y, angle),
                e.z
            )
        }
        val points = (points1 + points2 + points3 + points4 + points5 + points6).toMutableList()
        points.shuffle()

        this.points = points.toList()
    }
}