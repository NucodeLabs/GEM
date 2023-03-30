package ru.nucodelabs.geo.map

import org.junit.jupiter.api.Test
import ru.nucodelabs.geo.ves.calc.inverse.map.InverseSolver

internal class InverseTest {
    @Test
    fun myTest() {
        val points = mutableListOf<Point>()
        for (i in 0..400) {
            points.add(Point(0, i))
            points.add(Point(i, 0))
            points.add(Point(400, i))
            points.add(Point(i, 400))
        }

        val initialPoints = Pair(Point(-100,-100), Point(600, 600))

        val solver = InverseSolver(points)
        val e = solver.getOptimizedAngles(initialPoints)
        println(e)
    }
}