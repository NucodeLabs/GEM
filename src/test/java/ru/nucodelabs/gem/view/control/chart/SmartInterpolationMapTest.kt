package ru.nucodelabs.gem.view.control.chart

import com.google.inject.Guice
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.nucodelabs.gem.config.AppModule
import ru.nucodelabs.gem.view.FXTest
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.geo.ves.calc.interpolation.AnisotropyTestData
import ru.nucodelabs.kfx.ext.observableListOf
import ru.nucodelabs.kfx.ext.toObservableList

internal class SmartInterpolationMapTest : FXTest() {

    class TestApp : Application() {
        override fun start(primaryStage: Stage) {
            val injector = Guice.createInjector(AppModule())
            primaryStage.apply {
                scene = Scene(
                    VBox(
                        SmartInterpolationMap(
                            xAxis = NumberAxis().apply { isAutoRanging = true },
                            yAxis = NumberAxis().apply { isAutoRanging = true },
                            colorMapper = injector.getInstance(ColorMapper::class.java).apply {
                                minValue = 10.0
                                maxValue = 400.0
                            }
                        ).apply {
                            data = observableListOf(
                                XYChart.Series(AnisotropyTestData.points.toObservableList())
                            )
                        }
                    )
                )

                show()
            }
        }
    }

    @Test
    @Disabled
    fun launchApp() {
        Application.launch(TestApp::class.java)
    }
}