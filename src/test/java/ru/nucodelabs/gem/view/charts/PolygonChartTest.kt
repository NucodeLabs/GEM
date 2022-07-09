package ru.nucodelabs.gem.view.charts

import javafx.application.Application
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import ru.nucodelabs.gem.extensions.fx.observableListOf

internal class PolygonChartTest {
    companion object Init {
        @BeforeAll
        @JvmStatic
        fun initJfxRunTime(): Unit {
            Platform.startup { }
        }
    }

    @Test
//    @Disabled
    fun launchApp() {
        Application.launch(TestApp::class.java)
    }

    class TestApp : Application() {
        override fun start(primaryStage: Stage) {
            val chart = PolygonChart(
                NumberAxis(10.0, 100.0, 1.0),
                NumberAxis(10.0, 100.0, 1.0)
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
                    }
                )
            )
            primaryStage.show()

            chart.seriesPolygons[series]?.apply { fill = Color.GREENYELLOW }
        }

    }
}