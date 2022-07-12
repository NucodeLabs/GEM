package ru.nucodelabs.algorithms.interpolation

import javafx.scene.chart.XYChart

interface InterpolationDataParser {
    fun parse()

    fun getMissedPoints(): List<List<XYChart.Data<Double, Double>>>

    fun getGridX(): DoubleArray
    fun getGridY(): DoubleArray
    fun getGridF(): Array<DoubleArray>

    fun getX(): DoubleArray
    fun getY(): DoubleArray
    fun getF(): DoubleArray
}