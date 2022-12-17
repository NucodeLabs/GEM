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
import ru.nucodelabs.gem.app.AppModule
import ru.nucodelabs.gem.util.fx.observableListOf
import ru.nucodelabs.gem.util.fx.toObservableList
import ru.nucodelabs.gem.view.FXTest
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.gem.view.control.chart.log.PseudoLogarithmicAxis
import ru.nucodelabs.geo.ves.calc.interpolation.AnisotropyTestData

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