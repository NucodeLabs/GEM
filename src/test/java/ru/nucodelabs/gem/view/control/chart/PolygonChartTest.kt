package ru.nucodelabs.gem.view.control.chart

import javafx.application.Application
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.Scene
import javafx.scene.chart.ValueAxis
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.nucodelabs.gem.extensions.fx.observableListOf
import ru.nucodelabs.gem.view.FXTest
import ru.nucodelabs.gem.view.control.chart.log.LogarithmicAxis

internal class PolygonChartTest : FXTest() {

    @Test
    @Disabled
    fun launchApp() {
        Application.launch(TestApp::class.java)
    }

    class TestApp : Application() {
        override fun start(primaryStage: Stage) {
            val chart = PolygonChart(
                xAxis = LogarithmicAxis(10.0, 100.0).apply {
                    label = "X Axis"
                    inverted = true
                },
                yAxis = LogarithmicAxis(10.0, 1000.0).apply {
                    label = "Y Axis"
                    inverted = true
                }
            )

            chart.data += Series(
                observableListOf(
                    XYChart.Data(25.0, 25.0),
                    XYChart.Data(50.0, 25.0),
                    XYChart.Data(50.0, 50.0),
                    XYChart.Data(25.0, 50.0),
                )
            )
            val series: Series<Number, Number> = Series(
                observableListOf(
                    XYChart.Data(90.0, 90.0),
                    XYChart.Data(80.0, 80.0),
                    XYChart.Data(60.0, 90.0),
                )
            )
            chart.data += series
            val zoom = ZoomAxis(chart.xAxis as ValueAxis<Number>, chart.yAxis as ValueAxis<Number>)

            chart.onScroll = EventHandler { e ->
                    val position: Pair<Double, Double> = Pair(
                        chart.xAxis.sceneToLocal(Point2D(e.sceneX, e.sceneY)).x/chart.xAxis.length,
                        chart.yAxis.sceneToLocal(Point2D(e.sceneX, e.sceneY)).y/chart.yAxis.length
                    )
                    val dY = e.deltaY
                    val scale = 1.0 + dY / chart.yAxis.length
                    zoom.zoom(scale, position)
            }

            primaryStage.scene = Scene(
                VBox(
                    chart,
                    Button("Move values").apply {
                        onAction = EventHandler {
                            chart.data[0].data.forEach { it.xValue = it.xValue.toDouble() + 5.0 }
                        }
                    },
                    Button("Change 1 Data values").apply {
                        onAction = EventHandler {
                            chart.data[0].data.first().also { it.xValue = it.xValue.toDouble() + 5.0 }
                        }
                    },
                    Button("+").apply {
                        onAction = EventHandler {
                            zoom.zoom(1.1, Pair(0.3, 0.3))
                            println("${(chart.yAxis as ValueAxis<Number>).lowerBound}, ${(chart.yAxis as ValueAxis<Number>).upperBound}")
                        }
                    },
                    Button("-").apply {
                        onAction = EventHandler {
                            zoom.zoom(0.9, Pair(0.3, 0.3))
                            println("${(chart.yAxis as ValueAxis<Number>).lowerBound}, ${(chart.yAxis as ValueAxis<Number>).upperBound}")
                        }
                    }
                )
            )
            primaryStage.show()
            chart.seriesPolygons[series]?.apply { fill = Color.GREENYELLOW }
        }
    }
}