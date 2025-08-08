package ru.nucodelabs.geo.ves.calc.interpolation

import javafx.scene.chart.XYChart
import org.apache.commons.math3.analysis.interpolation.BicubicInterpolatingFunction
import ru.nucodelabs.util.std.exp10
import kotlin.math.log10

class SmartInterpolator(
    private val spatialInterpolator: SpatialInterpolator,
    private val regularGridInterpolator: RegularGridInterpolator
) : Interpolator2D {
    private lateinit var x: Array<Double>
    private lateinit var y: Array<Double>
    private lateinit var f: Array<Array<Double?>>

    private var xRange = Pair(Double.NaN, Double.NaN)
    private var yRange = Pair(Double.NaN, Double.NaN)

    private lateinit var bicubicInterpolatingFunction: BicubicInterpolatingFunction

    fun build(unorderedPoints: List<XYChart.Data<Double, Double>>) {
        buildLeakyGrid(unorderedPoints)
        setXYRanges()

        gridToLogValues()

        buildSpatial()

        buildRegularGrid()

        buildInterpolatingFunction()
    }

    private fun setXYRanges() {
        this.xRange = Pair(this.x.first(), this.x.last())
        this.yRange = Pair(this.y.first(), this.y.last())
    }

    private fun buildLeakyGrid(points: List<XYChart.Data<Double, Double>>) {
        val xPoints = points.map { it.xValue }.distinct().sorted()
        val yPoints = points.map { it.yValue }.distinct().sorted()

        this.x = xPoints.toTypedArray()
        this.y = yPoints.toTypedArray()
        this.f = Array(x.size) { Array(y.size) { null } }

        for (point in points) {
            val xIdx = x.indexOf(point.xValue)
            val yIdx = y.indexOf(point.yValue)

            this.f[xIdx][yIdx] = point.extraValue as Double
        }
    }

    private fun gridToLogValues() {
        for (xIdx in this.f.indices) {
            for (yIdx in this.f[xIdx].indices) {
                if (this.f[xIdx][yIdx] == null) {
                    continue
                }
                if (this.f[xIdx][yIdx]!! < 0) {
                    throw RuntimeException("gridToLogValues < 0 error")
                }
                this.f[xIdx][yIdx] = log10(this.f[xIdx][yIdx]!!)
            }
        }
    }

    private fun buildSpatial() {
        val x = mutableListOf<Double>()
        val y = mutableListOf<Double>()
        val f = mutableListOf<Double>()

        for (xIdx in this.f.indices) {
            for (yIdx in this.f[xIdx].indices) {
                if (this.f[xIdx][yIdx] != null) {
                    x.add(this.x[xIdx])
                    y.add(this.y[yIdx])
                    f.add(this.f[xIdx][yIdx]!!)
                }
            }
        }

        this.spatialInterpolator.build(x.toDoubleArray(), y.toDoubleArray(), f.toDoubleArray())
    }

    private fun buildRegularGrid() {
        for (xIdx in this.f.indices) {
            for (yIdx in this.f[xIdx].indices) {
                if (this.f[xIdx][yIdx] == null) {
                    this.f[xIdx][yIdx] = spatialInterpolator.interpolate(this.x[xIdx], this.y[yIdx])
                }
            }
        }
    }

    private fun buildInterpolatingFunction() {
        val f = this.f.map { it.requireNoNulls().toDoubleArray() }
        bicubicInterpolatingFunction =
            regularGridInterpolator.interpolate(this.x.toDoubleArray(), this.y.toDoubleArray(), f.toTypedArray())
    }

    private fun isInRange(x: Double, y: Double): Boolean {
        if (x in xRange.first..xRange.second && y in yRange.first..yRange.second) {
            return true
        }
        return false
    }

    override fun getValue(x: Double, y: Double): Double {
        if (!isInRange(x, y)) {
//            throw RuntimeException("getValue not in range")
            return -1.0
        }
        return exp10(bicubicInterpolatingFunction.value(x, y))
    }
}