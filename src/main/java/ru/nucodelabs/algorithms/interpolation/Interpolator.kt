package ru.nucodelabs.algorithms.interpolation

import javafx.scene.chart.XYChart
import org.apache.commons.math3.analysis.interpolation.BicubicInterpolatingFunction

class Interpolator (
    private val inputData: List<List<XYChart.Data<Double, Double>>>,
    private var interpolationParser: InterpolationParser = InterpolationParser(inputData)
) {
    private lateinit var grid: List<List<XYChart.Data<Double, Double>>>
    private lateinit var missedPoints: List<List<XYChart.Data<Double, Double>>>

    private lateinit var adjustedGrid: MutableList<MutableList<XYChart.Data<Double, Double>>>

    private lateinit var bicubicInterpolatingFunction: BicubicInterpolatingFunction
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
//        for (picket in missedPoints) {
//            for (point in picket) {
//                val f = spatialInterpolator.interpolate(point.xValue, point.yValue)
//            }
//        }
        for (point in missedPoints) {
            val xyPoint = adjustedGrid[point.first][point.second]
            if (xyPoint.extraValue != -1)
                throw RuntimeException("Missed point value != -1")
            val f = spatialInterpolator.interpolate(xyPoint.xValue, xyPoint.yValue)
            adjustedGrid[point.first][point.second] = XYChart.Data(xyPoint.xValue, xyPoint.yValue, f)
        }
    }

    private fun interpolateGrid() {
        initGrid()
        adjustGrid()
        interpolationParser = InterpolationParser(adjustedGrid) //Добавить bool
        val regularGridInterpolator = ApacheInterpolator2D()
        bicubicInterpolatingFunction = regularGridInterpolator.interpolate(
            interpolationParser.getGridX(),
            interpolationParser.getGridY(),
            interpolationParser.getGridF()
        )
    }

    init {
        interpolateGrid()
    }

    fun getValue(x: Double, y: Double): Double {
        //TODO: check range
        return bicubicInterpolatingFunction.value(x, y)
    }

    private fun checkRange(x: Double): Boolean {
        //TODO: check range
        return true
    }

}