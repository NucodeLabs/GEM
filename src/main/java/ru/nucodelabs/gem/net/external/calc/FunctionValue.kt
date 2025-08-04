package ru.nucodelabs.gem.net.external.calc

import org.apache.commons.math3.analysis.MultivariateFunction
import ru.nucodelabs.geo.anisotropy.calc.map.Point
import ru.nucodelabs.gem.util.fx.Point as PointFX

class FunctionValue(redPoints: List<Point>) : MultivariateFunction {

    private val function = DistanceToRectangle(redPoints)

    /**
     * values: Angles: x1, y1, x2, y2
     */
    override fun value(values: DoubleArray): Double {
        return function.apply(Pair(PointFX(values[0], values[1]), PointFX(values[2], values[3])))
    }
}