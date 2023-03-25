package ru.nucodelabs.geo.ves.calc.interpolation

import javafx.scene.chart.XYChart
import ru.nucodelabs.ShiraPicket
import ru.nucodelabs.geo.map.xFromCenter
import ru.nucodelabs.geo.map.yFromCenter

object AnisotropyTestData {
    val points: List<XYChart.Data<Number, Number>>
    init {
        val picket = ShiraPicket.picket
        val expData = picket.sortedExperimentalData
        val points1 = expData.map { e -> XYChart.Data(100.0, e.ab2, e.resistanceApparent) }
        var angle = 60.0
        val points2 = points1.map { e ->
            XYChart.Data<Number, Number>(
                (xFromCenter(e.yValue, angle) + e.xValue),
                yFromCenter(e.yValue, angle),
                (e.extraValue as Double)
            )
        }
        angle = 120.0
        val points3 = points1.map { e ->
            XYChart.Data<Number, Number>(
                (xFromCenter(e.yValue, angle) + e.xValue),
                yFromCenter(e.yValue, angle),
                (e.extraValue as Double)
            )
        }
        angle = 180.0
        val points4 = points1.map { e ->
            XYChart.Data<Number, Number>(
                (xFromCenter(e.yValue, angle) + e.xValue),
                yFromCenter(e.yValue, angle),
                (e.extraValue as Double)
            )
        }
        angle = -60.0
        val points5 = points1.map { e ->
            XYChart.Data<Number, Number>(
                (xFromCenter(e.yValue, angle) + e.xValue),
                yFromCenter(e.yValue, angle),
                (e.extraValue as Double)
            )
        }
        angle = -120.0
        val points6 = points1.map { e ->
            XYChart.Data<Number, Number>(
                (xFromCenter(e.yValue, angle) + e.xValue),
                yFromCenter(e.yValue, angle),
                (e.extraValue as Double)
            )
        }
        val points = (points1 + points2 + points3 + points4 + points5 + points6).toMutableList()
        points.shuffle()

        @Suppress("UNCHECKED_CAST")
        this.points = points.toList() as List<XYChart.Data<Number, Number>>
    }
}