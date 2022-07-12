package ru.nucodelabs.algorithms.interpolation

import javafx.scene.chart.XYChart

class Interpolator (
    private val inputData: List<List<XYChart.Data<Double, Double>>>,
    private var interpolationParser: InterpolationDataParser = InterpolationParser(inputData)
) {

    private fun adjustGrid() {

    }

}