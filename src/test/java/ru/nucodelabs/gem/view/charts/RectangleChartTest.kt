package ru.nucodelabs.gem.view.charts

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.chart.NumberAxis
import javafx.scene.paint.Color
import javafx.stage.Stage
import org.junit.jupiter.api.Test

internal class RectangleChartTest {

    class TestApp : Application() {
        override fun start(primaryStage: Stage) {
            val chart = RectangleChart(
                NumberAxis(0.0, 100.0, 1.0),
                NumberAxis(0.0, 100.0, 1.0)
            )
            primaryStage.scene = Scene(chart)
            primaryStage.show()

            chart.addRectangle(25.0, 30.0, 10.0, 20.0) { fill = Color.BLACK }
        }
    }

    @Test
    fun addRectangle() {
        Application.launch(TestApp::class.java)
    }
}