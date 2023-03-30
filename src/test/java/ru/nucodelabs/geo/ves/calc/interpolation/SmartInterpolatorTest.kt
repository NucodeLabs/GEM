package ru.nucodelabs.geo.ves.calc.interpolation

import javafx.scene.chart.XYChart
import org.junit.jupiter.api.Test
import ru.nucodelabs.geo.ves.calc.interpolation.AnisotropyTestData.points

internal class SmartInterpolatorTest {

    @Suppress("UNCHECKED_CAST")
    @Test
    fun build() {
        val smartInterpolator = SmartInterpolator(RBFSpatialInterpolator(), ApacheInterpolator2D())
        smartInterpolator.build(points as List<XYChart.Data<Double, Double>>)
    }

    @Test
    fun getValue() {
    }
}