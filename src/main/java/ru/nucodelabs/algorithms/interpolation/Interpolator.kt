package ru.nucodelabs.algorithms.interpolation

import javafx.scene.chart.XYChart
import org.apache.commons.math3.analysis.interpolation.BicubicInterpolatingFunction
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction
import ru.nucodelabs.gem.extensions.math.exp10

class Interpolator(
    private val inputData: List<List<XYChart.Data<Double, Double>>>,
    private var interpolationParser: InterpolationParser = InterpolationParser(inputData)
) {
    private lateinit var grid: List<List<XYChart.Data<Double, Double>>>

    private lateinit var missedPoints: List<List<XYChart.Data<Double, Double>>>

    private var adjustedGrid: MutableList<MutableList<XYChart.Data<Double, Double>>> = arrayListOf()

    private lateinit var bicubicInterpolatingFunction: BicubicInterpolatingFunction

    private var leftSideSplineFunction: PolynomialSplineFunction

    private lateinit var rightSideSplineFunction: PolynomialSplineFunction

    init {
        initGrid()
        adjustGrid()
        interpolationParser.gridToLogValues(adjustedGrid)
        leftSideSplineFunction = interpolateSide(grid[0])
        if (adjustedGrid.size != 1) {
            interpolateGrid()
            rightSideSplineFunction = interpolateSide(grid.last())
        }
    }

    private fun gridValuesToLog() {

    }

    private fun initGrid() {
        interpolationParser.parse()
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
        interpolationParser.parse()
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

    fun getValue(x: Double, y: Double): Double {
        return when (getRange(x)) {
            Side.LEFT -> exp10(interpolateSidePoint(y, leftSideSplineFunction))
            Side.MIDDLE -> exp10(bicubicInterpolatingFunction.value(x, y))
            Side.RIGHT -> exp10(interpolateSidePoint(y, rightSideSplineFunction))
        }
    }

    fun getValue(y: Double): Double {
        return exp10(leftSideSplineFunction.value(y))
    }

    private fun getRange(x: Double): Side {
        if (x < adjustedGrid[0][0].xValue) return Side.LEFT
        if (x > adjustedGrid.last()[0].xValue) return Side.RIGHT
        return Side.MIDDLE
    }
}

private enum class Side {
    LEFT,
    MIDDLE,
    RIGHT
}