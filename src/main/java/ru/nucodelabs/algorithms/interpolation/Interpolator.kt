package ru.nucodelabs.algorithms.interpolation

import javafx.scene.chart.XYChart

class Interpolator (
    private val inputData: List<List<XYChart.Data<Double, Double>>>,
    private var interpolationParser: InterpolationDataParser = InterpolationParser(inputData)
) {
    private lateinit var grid: List<List<XYChart.Data<Double, Double>>>
    private lateinit var missedPoints: List<List<XYChart.Data<Double, Double>>>
    private fun initGrid() {
        interpolationParser.parse()
        grid = interpolationParser.getGrid()
        missedPoints = interpolationParser.getMissedPoints()
    }

    private fun adjustGrid() {

    }

}