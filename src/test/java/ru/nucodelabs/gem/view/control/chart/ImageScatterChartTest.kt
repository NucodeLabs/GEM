package ru.nucodelabs.gem.view.control.chart

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.chart.NumberAxis
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.layout.*
import javafx.stage.Stage
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

import ru.nucodelabs.gem.view.FXTest

internal class ImageScatterChartTest : FXTest() {

    class TestApp : Application() {
        override fun start(primaryStage: Stage?) {
            primaryStage?.apply {
                val chart = ImageScatterChart(
                    NumberAxis(),
                    NumberAxis(),
//                    Image("https://static-maps.yandex.ru/1.x/?ll=37.620070,55.753630&size=450,450&z=13&l=sat"),
                )
                scene = Scene(
                    VBox().apply {
                        VBox.setVgrow(this, Priority.ALWAYS)
                        children.addAll(
                            Button("change bg").apply {
                                onAction = EventHandler {
                                    chart.image =
                                        Image("https://static-maps.yandex.ru/1.x/?ll=37.620070,55.753630&size=400,50&z=13&l=sat")
                                }
                            },
                            chart,
                        )
                    }
                )
            }?.show()
        }
    }

    @Test
    @Disabled
    fun launchApp() {
        Application.launch(TestApp::class.java)
    }
}
