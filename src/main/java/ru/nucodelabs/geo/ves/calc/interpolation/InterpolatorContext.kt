package ru.nucodelabs.geo.ves.calc.interpolation

import ru.nucodelabs.util.Point
import kotlin.math.log10

class InterpolatorContext(inputImmutableData: List<List<Point>>) {

    private lateinit var gridX: DoubleArray
    private lateinit var gridY: DoubleArray
    private lateinit var gridF: Array<DoubleArray>
    private lateinit var x: DoubleArray
    private lateinit var y: DoubleArray
    private lateinit var f: DoubleArray

    private var inputData: MutableList<MutableList<Point>> = arrayListOf()

    private var missedPoints: MutableList<MutableList<Point>> = arrayListOf()

    private val uniquePoints: MutableList<Double> = arrayListOf()

    private val grid: MutableList<MutableList<Point>> = arrayListOf()

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

    fun minY(): Double {
        return grid[0][0].y
    }

    fun maxY(): Double {
        return grid[0].last().y
    }

    fun minX(): Double {
        return grid[0][0].x
    }

    fun maxX(): Double {
        return grid.last()[0].x
    }

    fun gridToLogValues(grid: MutableList<MutableList<Point>>) {
        for (picketIdx in grid.indices) {
            for (abIdx in grid[picketIdx].indices) {
                val (x, y, z) = grid[picketIdx][abIdx]
                if (z >= 0) grid[picketIdx][abIdx] = Point(x, y, log10(z))
            }
        }
    }

    fun getMissedPositions(): MutableList<Pair<Int, Int>> {
        return missedPositions
    }

    fun copyData(listFrom: List<List<Point>>, listTo: MutableList<MutableList<Point>>) {
        for (picketIdx in listFrom.indices) {
            listTo.add(arrayListOf())
            for (ab in listFrom[picketIdx]) {
                listTo[picketIdx].add(ab.copy())
            }
        }
    }

    private fun checkMissedPoints() {
        for (picketIdx in missedPoints.indices) {
            var abIdx = missedPoints[picketIdx].size
            while (--abIdx >= 0) {
                if (missedPoints[picketIdx][abIdx].z != -1.0)
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
                x[cnt] = ab.x
                y[cnt] = ab.y
                f[cnt] = log10(ab.z)
                cnt++
            }
        }
    }

    private fun parseRegularGrid() {
        gridInit()
        gridX = DoubleArray(grid.size)
        gridY = DoubleArray(grid[0].size)
        gridF = Array(grid.size) { DoubleArray(grid[0].size) }
        for (picketIdx in grid.indices) {
            gridX[picketIdx] = grid[picketIdx][0].x
            for (abIdx in grid[picketIdx].indices) {
                gridF[picketIdx][abIdx] = grid[picketIdx][abIdx].z
            }
        }
        for (abIdx in grid[0].indices) {
            gridY[abIdx] = grid[0][abIdx].y
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
                if (!grid[picketIdx].any { e -> e.y == ab })
                    grid[picketIdx].add(Point(inputData[picketIdx][0].x, ab, -1.0))
            }
            grid[picketIdx].sortBy { it.y }
        }
    }

    private fun parseUnique() {
        for (picket in inputData) {
            for (ab in picket) {
                if (!uniquePoints.contains(ab.y))
                    uniquePoints.add(ab.y)
            }
        }
        uniquePoints.sortBy { it }
    }

    private fun removeSame() {
        for (picket in inputData) {
            var size = picket.size - 1
            var abIdx = 0
            picket.sortBy { it.y }
            while (abIdx < size) {
                if (picket[abIdx].y == picket[abIdx + 1].y) {
                    picket.removeAt(abIdx + 1)
                    size--
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

    fun getMissedPoints(): MutableList<MutableList<Point>> {
        return missedPoints
    }

    fun getGrid(): MutableList<MutableList<Point>> {
        return grid
    }
}