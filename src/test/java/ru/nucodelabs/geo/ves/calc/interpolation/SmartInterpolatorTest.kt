package ru.nucodelabs.geo.ves.calc.interpolation

import javafx.scene.chart.XYChart
import org.junit.jupiter.api.Test

import ru.nucodelabs.ShiraPicket
import ru.nucodelabs.geo.map.xFromCenter
import ru.nucodelabs.geo.map.yFromCenter
import kotlin.random.Random

internal class SmartInterpolatorTest {

    @Test
    fun build() {
        val picket = ShiraPicket.picket
        val expData = picket.sortedExperimentalData
        val points1 = expData.map { e -> XYChart.Data(100.0, e.ab2, e.resistanceApparent) }
        var angle = 30.0
        val points2 = points1.map { e ->
            XYChart.Data(
                xFromCenter(e.yValue, angle) + e.xValue,
                yFromCenter(e.yValue, angle),
                (e.extraValue as Double)
            )
        }
        angle = 45.0
        val points3 = points1.map { e ->
            XYChart.Data(
                xFromCenter(e.yValue, angle) + e.xValue,
                yFromCenter(e.yValue, angle),
                (e.extraValue as Double)
            )
        }
        angle = -60.0
        val points4 = points1.map { e ->
            XYChart.Data(
                xFromCenter(e.yValue, angle) + e.xValue,
                yFromCenter(e.yValue, angle),
                (e.extraValue as Double)
            )
        }
        val points = (points1 + points2 + points3 + points4) as MutableList
        points.shuffle()

        val smartInterpolator = SmartInterpolator(KrigingInterpolator())
        smartInterpolator.build(points)

        println(smartInterpolator.getValue(200.0, 200.0))
    }

    @Test
    fun getValue() {
    }
}