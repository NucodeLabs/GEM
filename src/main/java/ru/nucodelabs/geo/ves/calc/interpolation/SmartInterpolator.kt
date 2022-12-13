package ru.nucodelabs.geo.ves.calc.interpolation

import javafx.scene.chart.XYChart
import org.apache.commons.math3.analysis.interpolation.BicubicInterpolatingFunction

class SmartInterpolator(
    private val spatialInterpolator: SpatialInterpolator,
    private val regularGridInterpolator: RegularGridInterpolator
) {
    private lateinit var x: Array<Double>
    private lateinit var y: Array<Double>
    private lateinit var f: Array<Array<Double?>>

    private lateinit var bicubicInterpolatingFunction: BicubicInterpolatingFunction

    fun build(unorderedPoints: List<XYChart.Data<Double, Double>>) {
        val unorderedUniquePoints = toUniquePoints(unorderedPoints)
        buildLeakyGrid(unorderedUniquePoints)

        buildSpatial()

        buildRegularGrid()

        buildInterpolatingFunction()
    }

    private fun toUniquePoints(unorderedPoints: List<XYChart.Data<Double, Double>>): List<XYChart.Data<Double, Double>> {
        val uniquePoints = mutableListOf<XYChart.Data<Double, Double>>()
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

        return uniquePoints
    }

    private fun getGridSize(unorderedUniquePoints: List<XYChart.Data<Double, Double>>): Pair<Int, Int> {
        val hsX = HashSet<Double>()
        val hsY = HashSet<Double>()

        for (point in unorderedUniquePoints) {
            val x = point.xValue
            val y = point.yValue

            if (hsX.contains(x) && hsY.contains(y)) {
                throw RuntimeException("Such point is already exist")
            } else if (hsX.contains(x)) {
                hsY.add(y)
            } else {
                hsX.add(x)
            }
        }

        return Pair(hsX.size, hsY.size)
    }

    private fun buildLeakyGrid(unorderedPoints: List<XYChart.Data<Double, Double>>) {
        val gridSize = getGridSize(unorderedPoints)

        this.x = Array(gridSize.first) { Double.NaN }
        this.y = Array(gridSize.second) { Double.NaN }
        this.f = Array(gridSize.first) { Array(gridSize.second) { null } }

        var xCnt = 0
        var yCnt = 0

        for (point in unorderedPoints) {
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

            } else {
                val yPos = this.y.indexOf(y)
                this.x[xCnt] = x
                this.f[yPos][xCnt] = f
                xCnt++
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
}