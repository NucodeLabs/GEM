package ru.nucodelabs.geo.ves.calc.inverse.map

import ru.nucodelabs.gem.util.fx.Point as PointFX
import ru.nucodelabs.geo.map.Point
import java.util.function.Function
import kotlin.math.hypot

class DistanceToRectangle(private val points: List<Point>) : Function<Pair<PointFX<Double, Double>, PointFX<Double, Double>>, Double> {
    override fun apply(
        angles: Pair<PointFX<Double, Double>, PointFX<Double, Double>>
    ): Double {
        var value = 0.0
        for (point in points) {
            value += distanceToRectangle(point, angles)
        }
        return value
    }

    private fun distanceToRectangle(from: Point, points: Pair<PointFX<Double, Double>, PointFX<Double, Double>>): Double {
        val a = points.first
        val b = points.second
        val c = PointFX(a.xValue, b.yValue)
        val d = PointFX(b.xValue, a.yValue)
        val segments = listOf(Pair(a, c), Pair(c, b), Pair(b, d), Pair(d, a))
        val results = segments.map { segment -> distanceToSegment(from, segment) }
        return results.min()
    }

    private fun distanceToSegment(
        from: Point,
        segment: Pair<PointFX<Double, Double>, PointFX<Double, Double>>
    ): Double {
        return getDistance(
            from.x.toDouble(),
            from.y.toDouble(),
            segment.first.xValue,
            segment.first.yValue,
            segment.second.xValue,
            segment.second.yValue
        )
    }

    private fun getDistance(x: Double, y: Double, x1: Double, y1: Double, x2: Double, y2: Double): Double {
        val a = x - x1
        val b = y - y1
        val c = x2 - x1
        val d = y2 - y1

        val lenSq = c * c + d * d
        val param = if (lenSq != .0) { //in case of 0 length line
            val dot = a * c + b * d
            dot / lenSq
        } else {
            -1.0
        }

        val (xx, yy) = when {
            param < 0 -> x1 to y1
            param > 1 -> x2 to y2
            else -> x1 + param * c to y1 + param * d
        }

        val dx = x - xx
        val dy = y - yy
        return hypot(dx, dy)
    }
}