package ru.nucodelabs.algorithms.interpolation

import javafx.scene.chart.XYChart
import ru.nucodelabs.gem.view.color_palette.ColorPalette

class Interpolator (
    private val inputData: List<List<XYChart.Data<Double, Double>>>,
    private val colorPalette: ColorPalette
) {
    private lateinit var interpolationParser: InterpolationDataParser
}