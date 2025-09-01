package ru.nucodelabs.geo.ves.calc.interpolation

import org.apache.commons.math3.analysis.interpolation.BicubicInterpolatingFunction
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction
import ru.nucodelabs.util.Point
import ru.nucodelabs.util.std.exp10

class Interpolator(
    private var interpolatorContext: InterpolatorContext
) : Interpolator2D {
    private lateinit var grid: MutableList<MutableList<Point>>

    private lateinit var missedPoints: List<List<Point>>

    private var adjustedGrid: MutableList<MutableList<Point>> = arrayListOf()

    private lateinit var bicubicInterpolatingFunction: BicubicInterpolatingFunction

    private lateinit var leftSideSplineFunction: PolynomialSplineFunction

    private lateinit var rightSideSplineFunction: PolynomialSplineFunction

    private var leftSideInterpolated = true

    private var rightSideInterpolated = true

    init {
        initGrid()
        interpolatorContext.gridToLogValues(grid)
        adjustGrid()
        checkInterpolated()
        if (leftSideInterpolated) leftSideSplineFunction = interpolateSide(adjustedGrid[0])
        if (adjustedGrid.size != 1) {
            interpolateGrid()
            if (rightSideInterpolated) rightSideSplineFunction = interpolateSide(adjustedGrid.last())
        }
    }

    fun getValue(y: Double): Double {
        return if (leftSideInterpolated) {
            exp10(leftSideSplineFunction.value(y))
        } else {
            -1.0
        }
    }

    override fun getValue(x: Double, y: Double): Double {
        return when (getRange(x)) {
            Side.LEFT ->
                if (leftSideInterpolated) {
                    exp10(interpolateSidePoint(y, leftSideSplineFunction))
                } else {
                    -1.0
                }

            Side.MIDDLE -> exp10(bicubicInterpolatingFunction.value(x, y))
            Side.RIGHT ->
                if (rightSideInterpolated) {
                    exp10(interpolateSidePoint(y, rightSideSplineFunction))
                } else {
                    -1.0
                }
        }
    }

    private fun checkInterpolated() {
        if (adjustedGrid[0].size < 3) leftSideInterpolated = false
        if (adjustedGrid.last().size < 3) rightSideInterpolated = false
    }

    private fun initGrid() {
        grid = interpolatorContext.getGrid()
        missedPoints = interpolatorContext.getMissedPoints()
    }

    private fun adjustGrid() {
        val spatialInterpolator: SpatialInterpolator = RBFSpatialInterpolator()
        spatialInterpolator.build(
            interpolatorContext.getX(),
            interpolatorContext.getY(),
            interpolatorContext.getF()
        )
        interpolatorContext.copyData(grid, adjustedGrid)

        val missedPoints = interpolatorContext.getMissedPositions()
        for (point in missedPoints) {
            val xyPoint = adjustedGrid[point.first][point.second]
            if (xyPoint.z != -1.0)
                throw RuntimeException("Missed point value != -1")
            val f = spatialInterpolator.interpolate(xyPoint.x, xyPoint.y)
            adjustedGrid[point.first][point.second] = Point(xyPoint.x, xyPoint.y, f)
        }
    }

    private fun interpolateGrid() {
        interpolatorContext = InterpolatorContext(adjustedGrid)
        val regularGridInterpolator = ApacheInterpolator2D()
        bicubicInterpolatingFunction = regularGridInterpolator.interpolate(
            interpolatorContext.getGridX(),
            interpolatorContext.getGridY(),
            interpolatorContext.getGridF()
        )
    }

    private fun interpolateSide(picket: List<Point>): PolynomialSplineFunction {
        val y: DoubleArray = picket.map { point -> point.y }.toDoubleArray()
        val f: DoubleArray = picket.map { point -> point.z }.toDoubleArray()

        val interpolator1D: Interpolator1D = ApacheInterpolator1D()
        return interpolator1D.interpolate(y, f)
    }

    private fun interpolateSidePoint(y: Double, function: PolynomialSplineFunction): Double {
        return function.value(y)
    }

    private fun getRange(x: Double): Side {
        if (x < adjustedGrid[0][0].x)
            return Side.LEFT
        if (x > adjustedGrid.last()[0].x)
            return Side.RIGHT
        return Side.MIDDLE
    }
}

private enum class Side {
    LEFT,
    MIDDLE,
    RIGHT
}