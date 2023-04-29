package ru.nucodelabs.geo.map

import org.junit.jupiter.api.Test
import ru.nucodelabs.gem.net.external.calc.YandexInverseSolver
import ru.nucodelabs.geo.anisotropy.calc.map.Point
import kotlin.random.Random

internal class InverseTest {
    @Test
    fun myTest() {
        val points = mutableListOf<Point>()
        var range = 400
        var start = 0
        for (i in start..range) {
            points.add(Point(start, i))
            points.add(Point(i, start))
            points.add(Point(range, i))
            points.add(Point(i, range))
        }
//        range = 401
//        start = -1
//        for (i in start..range) {
//            points.add(Point(start, i))
//            points.add(Point(i, start))
//            points.add(Point(range, i))
//            points.add(Point(i, range))
//        }
//        range = 402
//        start = -2
//        for (i in start..range) {
//            points.add(Point(start, i))
//            points.add(Point(i, start))
//            points.add(Point(range, i))
//            points.add(Point(i, range))
//        }

        val points1 = mutableSetOf<Point>()
        for (i in 1..100) {
            points1.add(Point(Random.nextInt(-200, 800), Random.nextInt(-200, 800)))
        }

        points += points1

        val initialPoints = Pair(Point(180,180), Point(220, 220))

        val solver = YandexInverseSolver(points)
        val e = solver.getOptimizedAngles(initialPoints)
        println(e)
    }
}