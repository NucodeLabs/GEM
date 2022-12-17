package ru.nucodelabs.geo.ves.calc.interpolation

import javafx.scene.chart.XYChart
import org.junit.jupiter.api.Test

import ru.nucodelabs.ShiraPicket
import ru.nucodelabs.geo.map.xFromCenter
import ru.nucodelabs.geo.map.yFromCenter
import ru.nucodelabs.geo.ves.calc.interpolation.AnisotropyTestData.points
import kotlin.random.Random

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