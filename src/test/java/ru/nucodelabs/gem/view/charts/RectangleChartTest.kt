package ru.nucodelabs.gem.view.charts

import javafx.application.Application
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.chart.NumberAxis
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.nucodelabs.gem.view.charts.RectangleChart.Rectangle

internal class RectangleChartTest {

    @Test
    @Disabled
    fun launchApp() {
        Application.launch(TestApp::class.java)
    }

    class TestApp : Application() {
        private val chart = RectangleChart(
            NumberAxis(0.0, 100.0, 1.0),
            NumberAxis(0.0, 100.0, 1.0)
        )

        override fun start(primaryStage: Stage) {
            chart.dataRectangles += Rectangle(25.0, 30.0, 10.0, 20.0) { fill = Color.BLACK }
            chart.dataRectangles += Rectangle(80.0, 90.0, 50.0, 50.0) { fill = Color.RED }
            val yellow = Rectangle(-50.0, -40.0, 60.0, 50.0) { fill = Color.YELLOW }
            chart.dataRectangles += yellow

            val testBtn1 = Button("Change X/Y values for axes").apply {
                onAction = EventHandler {
                    val rect = chart.dataRectangles[1]
                    rect.xOnAxis -= 10
                    rect.yOnAxis -= 10
                }
            }

            val testBtn2 = Button("Change W/H values for axes").apply {
                onAction = EventHandler {
                    val rect = chart.dataRectangles[1]
                    rect.widthOnAxis -= 10
                    rect.heightOnAxis -= 10
                }
            }

            primaryStage.scene = Scene(VBox(chart, testBtn1, testBtn2))
            primaryStage.show()

            chart.dataRectangles.remove(yellow)
        }
    }

    private val chart = RectangleChart(
        NumberAxis(0.0, 100.0, 1.0),
        NumberAxis(0.0, 100.0, 1.0)
    )

    companion object Init {
        @BeforeAll
        @JvmStatic
        fun initJfxRuntime() {
            Platform.startup { }
        }
    }


    @Test
    fun addRectangle() {
        chart.dataRectangles += Rectangle(25.0, 30.0, 10.0, 20.0) { fill = Color.BLACK }
        chart.dataRectangles += Rectangle(80.0, 90.0, 50.0, 50.0) { fill = Color.RED }
        chart.dataRectangles += Rectangle(-50.0, -40.0, 60.0, 50.0) { fill = Color.YELLOW }
        chart.dataRectangles += Rectangle(-50.0, -40.0, 60.0, 50.0) { fill = Color.YELLOW }

        assertEquals(4, chart.rectangleSeries.size)
        assertEquals(4, chart.dataRectangles.size)
        assertEquals(4, chart.data.size)
    }

    @Test
    fun removeRectangle() {
        val addedRect = Rectangle(0.0, 0.0, 10.0, 10.0)
        chart.dataRectangles += addedRect
        chart.dataRectangles.remove(addedRect)

        assertTrue(chart.rectangleSeries.isEmpty())
        assertTrue(chart.data.isEmpty())
    }
}