package ru.nucodelabs.geo.ves.calc.interpolation

import javafx.scene.chart.XYChart

interface Interpolator2D {
    fun getValue(x: Double, y: Double): Double

    fun build(unorderedPoints: List<XYChart.Data<Double, Double>>)
}