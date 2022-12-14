package ru.nucodelabs.geo.ves.calc.interpolation

import javafx.scene.chart.XYChart
import org.apache.commons.math3.analysis.interpolation.BicubicInterpolatingFunction
import ru.nucodelabs.gem.util.std.exp10
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
        setXYRanges(unorderedPoints)
        val orderedUniquePoints = toOrderedUniquePoints(unorderedPoints)

        buildLeakyGrid(orderedUniquePoints)

        gridToLogValues()

        buildSpatial()

        buildRegularGrid()

        buildInterpolatingFunction()
    }

    private fun toOrderedUniquePoints(unorderedPoints: List<XYChart.Data<Double, Double>>): List<XYChart.Data<Double, Double>> {
        var uniquePoints = mutableListOf<XYChart.Data<Double, Double>>()
        val hs = HashSet<Pair<Double, Double>>()

        for (point in unorderedPoints) {
            val x = point.xValue
            val y = point.yValue

            if (hs.contains(Pair(x, y))) {
                continue
            }

            hs.add(Pair(x, y))
            uniquePoints.add(point)
        }

        uniquePoints = uniquePoints.sortedWith(compareBy({ it.xValue }, { it.xValue })) as MutableList<XYChart.Data<Double, Double>>

        return uniquePoints
    }

    private fun setXYRanges(points: List<XYChart.Data<Double, Double>>) {
        this.xRange = Pair(points.minBy { it.xValue }.xValue, points.maxBy { it.xValue }.xValue)
        this.yRange = Pair(points.minBy { it.yValue }.yValue, points.maxBy { it.yValue }.xValue)
    }

    private fun getGridSize(orderedUniquePoints: List<XYChart.Data<Double, Double>>): Pair<Int, Int> {
        val hsX = HashSet<Double>()
        val hsY = HashSet<Double>()

        for (point in orderedUniquePoints) {
            val x = point.xValue
            val y = point.yValue

            if (hsX.contains(x) && hsY.contains(y)) {
                throw RuntimeException("Such point is already exist")
            } else {
                hsX.add(x)
                hsY.add(y)
            }
        }

        return Pair(hsX.size, hsY.size)
    }

    private fun buildLeakyGrid(orderedUniquePoints: List<XYChart.Data<Double, Double>>) {
        val gridSize = getGridSize(orderedUniquePoints)

        this.x = Array(gridSize.first) { Double.NaN }
        this.y = Array(gridSize.second) { Double.NaN }
        this.f = Array(gridSize.first) { Array(gridSize.second) { null } }

        var xCnt = 0
        var yCnt = 0

        for (point in orderedUniquePoints) {
            val x = point.xValue
            val y = point.yValue
            val f = point.extraValue as Double

            if (this.x.contains(x) && this.y.contains(y)) {
                throw RuntimeException("Such point is already exist")
            } else if (this.x.contains(x)) {
                val xPos = this.x.indexOf(x)
                this.y[yCnt] = y
                this.f[xPos][yCnt] = f
                yCnt++
            } else if (this.y.contains(y)) {
                val yPos = this.y.indexOf(y)
                this.x[xCnt] = x
                this.f[yPos][xCnt] = f
                xCnt++
            } else {
                this.x[xCnt] = x
                this.y[yCnt] = y
                this.f[xCnt][yCnt] = f
                xCnt++
                yCnt++
            }
        }
        println()
    }

    private fun gridToLogValues() {
        for (xIdx in this.f.indices) {
            for (yIdx in this.f[xIdx].indices) {
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
            throw RuntimeException("getValue not in range")
        }
        return exp10(bicubicInterpolatingFunction.value(x, y))
    }
}