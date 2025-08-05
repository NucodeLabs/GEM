package ru.nucodelabs.gem.view.control.chart

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.chart.NumberAxis
import javafx.scene.paint.Color
import javafx.stage.Stage
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.nucodelabs.gem.view.FXTest
import ru.nucodelabs.kfx.ext.clear

internal class AbstractMapTest : FXTest() {

    class TestApp : Application() {
        override fun start(primaryStage: Stage) {
            primaryStage.scene = Scene(
                object : AbstractMap(
                    NumberAxis(),
                    NumberAxis()
                ) {
                    override fun layoutPlotChildren() {
                        super.layoutPlotChildren()
                        canvas.clear()
                        draw(canvas)
                    }

                    override fun layoutChildren() {
                        super.layoutChildren()
                    }

                    fun draw(canvas: Canvas) {
                        canvas.run {
                            graphicsContext2D.fill = Color.RED
                            graphicsContext2D.fillOval(0.0, 0.0, canvas.width, canvas.height)
                        }
                    }
                }
            )
            primaryStage.show()
        }
    }

    @Test
    @Disabled
    fun launchApp() {
        Application.launch(TestApp::class.java)
    }
}