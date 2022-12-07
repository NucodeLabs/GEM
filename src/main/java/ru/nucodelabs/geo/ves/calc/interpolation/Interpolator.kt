package ru.nucodelabs.geo.ves.calc.interpolation

import javafx.scene.chart.XYChart
import org.apache.commons.math3.analysis.interpolation.BicubicInterpolatingFunction
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction
import ru.nucodelabs.gem.util.std.exp10

class Interpolator(
    private var interpolationParser: InterpolationParser
) {
    private lateinit var grid: MutableList<MutableList<XYChart.Data<Double, Double>>>

    private lateinit var missedPoints: List<List<XYChart.Data<Double, Double>>>

    private var adjustedGrid: MutableList<MutableList<XYChart.Data<Double, Double>>> = arrayListOf()

    private lateinit var bicubicInterpolatingFunction: BicubicInterpolatingFunction

    private lateinit var leftSideSplineFunction: PolynomialSplineFunction

    private lateinit var rightSideSplineFunction: PolynomialSplineFunction

    private var leftSideInterpolated = true

    private var rightSideInterpolated = true

    init {
        initGrid()
        interpolationParser.gridToLogValues(grid)
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

    fun getValue(x: Double, y: Double): Double {
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
        grid = interpolationParser.getGrid()
        missedPoints = interpolationParser.getMissedPoints()
    }

    private fun adjustGrid() {
        val spatialInterpolator: SpatialInterpolator = RBFSpatialInterpolator(
            interpolationParser.getX(),
            interpolationParser.getY(),
            interpolationParser.getF()
        )
        interpolationParser.copyData(grid, adjustedGrid)

        val missedPoints = interpolationParser.getMissedPositions()
        for (point in missedPoints) {
            val xyPoint = adjustedGrid[point.first][point.second]
            if (xyPoint.extraValue != -1.0)
                throw RuntimeException("Missed point value != -1")
            val f = spatialInterpolator.interpolate(xyPoint.xValue, xyPoint.yValue)
            adjustedGrid[point.first][point.second] = XYChart.Data(xyPoint.xValue, xyPoint.yValue, f)
        }
    }

    private fun interpolateGrid() {
        interpolationParser = InterpolationParser(adjustedGrid)
        val regularGridInterpolator = ApacheInterpolator2D()
        bicubicInterpolatingFunction = regularGridInterpolator.interpolate(
            interpolationParser.getGridX(),
            interpolationParser.getGridY(),
            interpolationParser.getGridF()
        )
    }

    private fun interpolateSide(picket: List<XYChart.Data<Double, Double>>): PolynomialSplineFunction {
        val y: DoubleArray = picket.map { point -> point.yValue }.toDoubleArray()
        val f: DoubleArray = picket.map { point -> point.extraValue as Double }.toDoubleArray()

        val interpolator1D: Interpolator1D = ApacheInterpolator1D()
        return interpolator1D.interpolate(y, f)
    }

    private fun interpolateSidePoint(y: Double, function: PolynomialSplineFunction): Double {
        return function.value(y)
    }

    private fun getRange(x: Double): Side {
        if (x < adjustedGrid[0][0].xValue)
            return Side.LEFT
        if (x > adjustedGrid.last()[0].xValue)
            return Side.RIGHT
        return Side.MIDDLE
    }
}

private enum class Side {
    LEFT,
    MIDDLE,
    RIGHT
}