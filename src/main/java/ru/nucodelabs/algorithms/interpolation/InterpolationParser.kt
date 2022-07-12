package ru.nucodelabs.algorithms.interpolation

import javafx.scene.chart.XYChart

class InterpolationParser (private var inputData: MutableList<List<XYChart.Data<Double, Double>>>) :
    InterpolationDataParser {

    private lateinit var gridX: DoubleArray
    private lateinit var gridY: DoubleArray
    private lateinit var gridF: Array<DoubleArray>
    private lateinit var x: DoubleArray
    private lateinit var y: DoubleArray
    private lateinit var f: DoubleArray

    private var missedPoints: MutableList<MutableList<XYChart.Data<Double, Double>>> = arrayListOf()

    private val uniquePoints: MutableList<XYChart.Data<Double, Double>> = arrayListOf()

    private val grid: MutableList<MutableList<XYChart.Data<Double, Double>>> = arrayListOf()

    override fun parse() {
        checkData()
        removeSame()
        parseUnique()
        parseRegularGrid()
        parseSpatial()
        checkMissedPoints()
    }

    private fun checkMissedPoints() {
        missedPoints = grid
        missedPoints.map { uniquePoints - it.toSet() }
    }

    private fun parseSpatial() {
        val pointsCnt = inputData.sumOf { it.size }
        x = DoubleArray(pointsCnt)
        y = DoubleArray(pointsCnt)
        f = DoubleArray(pointsCnt)
        val cnt = 0
        for (picketIdx in inputData.indices) {
            for (ab in inputData[picketIdx]) {
                x[cnt] = ab.xValue
                y[cnt] = ab.yValue
                f[cnt] = ab.extraValue as Double
            }
        }
    }

    private fun parseRegularGrid() {
        gridInit()
        gridX = DoubleArray(grid.size)
        gridY = DoubleArray(grid[0].size)
        gridF = Array(grid.size) {DoubleArray(grid[0].size)}
        for (picketIdx in grid.indices) {
            gridX[picketIdx] = grid[picketIdx][0].xValue
            for (abIdx in grid[picketIdx].indices) {
                gridF[picketIdx][abIdx] = grid[picketIdx][abIdx].extraValue as Double
            }
        }
        for (abIdx in grid[0].indices) {
            gridY[abIdx] = grid[0][abIdx].yValue
        }
    }

    private fun checkData() {
        for (picket in inputData) {
            if (picket.isEmpty()) throw RuntimeException("One of pickets is empty")
        }
    }

    private fun gridInit() {
        for (i in inputData.indices) {
            grid.add(uniquePoints)
        }
    }

    private fun parseUnique() {
        for (picket in inputData) {
            for (ab in picket) {
                if (!uniquePoints.contains(ab))
                    uniquePoints.add(ab)
            }
        }
        uniquePoints.sortBy { it.xValue }
    }

    private fun removeSame() {
        for (i in inputData.indices) {
            inputData[i] = inputData[i].distinct()
        }
    }

    override fun getGridX(): DoubleArray {
        return gridX
    }

    override fun getGridY(): DoubleArray {
        return gridY
    }

    override fun getGridF(): Array<DoubleArray> {
        return gridF
    }

    override fun getX(): DoubleArray {
        return x
    }

    override fun getY(): DoubleArray {
        return y
    }

    override fun getF(): DoubleArray {
        return f
    }

    override fun getMissedPoints(): MutableList<MutableList<XYChart.Data<Double, Double>>> {
        return missedPoints
    }
}