package ru.nucodelabs.geo.ves.calc.interpolation

import javafx.scene.chart.XYChart
import kotlin.math.log10

class InterpolationParser (private val inputImmutableData: List<List<XYChart.Data<Double, Double>>>) {

    private lateinit var gridX: DoubleArray
    private lateinit var gridY: DoubleArray
    private lateinit var gridF: Array<DoubleArray>
    private lateinit var x: DoubleArray
    private lateinit var y: DoubleArray
    private lateinit var f: DoubleArray

    private var inputData: MutableList<MutableList<XYChart.Data<Double, Double>>> = arrayListOf()

    private var missedPoints: MutableList<MutableList<XYChart.Data<Double, Double>>> = arrayListOf()

    private val uniquePoints: MutableList<Double> = arrayListOf()

    private val grid: MutableList<MutableList<XYChart.Data<Double, Double>>> = arrayListOf()

    private val missedPositions: MutableList<Pair<Int, Int>> = arrayListOf()

    init {
        checkData()
        copyData(inputImmutableData, inputData)
        removeSame()
        copyData(inputData, grid)
        parseUnique()
        parseRegularGrid()
        parseSpatial()
        copyData(grid, missedPoints)
        checkMissedPoints()
    }

    fun minResistance(): Double {
        return grid[0][0].yValue
    }

    fun maxResistance(): Double {
        return grid[0].last().yValue
    }

    fun minAB(): Double {
        return grid[0][0].xValue
    }

    fun maxAB(): Double {
        return grid.last()[0].xValue
    }

    fun gridToLogValues(grid: MutableList<MutableList<XYChart.Data<Double, Double>>>) {
        for (picketIdx in grid.indices) {
            for (abIdx in grid[picketIdx].indices) {
                if (grid[picketIdx][abIdx].extraValue as Double >= 0)
                    grid[picketIdx][abIdx].extraValue = log10(grid[picketIdx][abIdx].extraValue as Double)
            }
        }
    }

    fun getMissedPositions(): MutableList<Pair<Int, Int>> {
        return missedPositions
    }

    fun copyData(listFrom: List<List<XYChart.Data<Double, Double>>>, listTo: MutableList<MutableList<XYChart.Data<Double, Double>>>) {
        for (picketIdx in listFrom.indices) {
            listTo.add(arrayListOf())
            for (ab in listFrom[picketIdx]) {
                listTo[picketIdx].add(XYChart.Data(ab.xValue, ab.yValue, ab.extraValue))
            }
        }
    }

    private fun checkMissedPoints() {
        for (picketIdx in missedPoints.indices) {
            var abIdx = missedPoints[picketIdx].size
            while (--abIdx >= 0) {
                if (missedPoints[picketIdx][abIdx].extraValue as Double != -1.0)
                    missedPoints[picketIdx].removeAt(abIdx)
                else
                    missedPositions.add(Pair(picketIdx, abIdx))
            }
        }
    }

    private fun parseSpatial() {
        val pointsCnt = inputData.sumOf { it.size }
        x = DoubleArray(pointsCnt)
        y = DoubleArray(pointsCnt)
        f = DoubleArray(pointsCnt)
        var cnt = 0
        for (picketIdx in inputData.indices) {
            for (ab in inputData[picketIdx]) {
                x[cnt] = ab.xValue
                y[cnt] = ab.yValue
                f[cnt] = log10(ab.extraValue as Double)
                cnt++
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
        for (picketIdx in inputData.indices) {
            for (ab in uniquePoints) {
                if (!grid[picketIdx].any {e -> e.yValue == ab})
                    grid[picketIdx].add(XYChart.Data(inputData[picketIdx][0].xValue, ab, -1.0))
            }
            grid[picketIdx].sortBy { it.yValue }
        }
    }

    private fun parseUnique() {
        for (picket in inputData) {
            for (ab in picket) {
                if (!uniquePoints.contains(ab.yValue))
                    uniquePoints.add(ab.yValue)
            }
        }
        uniquePoints.sortBy { it }
    }

    private fun removeSame() {
        for (picket in inputData) {
            var size = picket.size - 1
            var abIdx = 0
            picket.sortBy { it.yValue }
            while (abIdx < size) {
                if (picket[abIdx].yValue == picket[abIdx + 1].yValue) {
                    picket.removeAt(abIdx + 1)
                    size --
                }
                abIdx++
            }
        }
    }

    fun getGridX(): DoubleArray {
        return gridX
    }

    fun getGridY(): DoubleArray {
        return gridY
    }

    fun getGridF(): Array<DoubleArray> {
        return gridF
    }

    fun getX(): DoubleArray {
        return x
    }

    fun getY(): DoubleArray {
        return y
    }

    fun getF(): DoubleArray {
        return f
    }

    fun getMissedPoints(): MutableList<MutableList<XYChart.Data<Double, Double>>> {
        return missedPoints
    }

    fun getGrid(): MutableList<MutableList<XYChart.Data<Double, Double>>> {
        return grid
    }
}