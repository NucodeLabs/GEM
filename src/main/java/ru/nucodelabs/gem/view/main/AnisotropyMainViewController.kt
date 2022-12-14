package ru.nucodelabs.gem.view.main

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.layout.VBox
import javafx.stage.Stage
import ru.nucodelabs.gem.view.AbstractController
import ru.nucodelabs.gem.view.charts.AnisotropyMapImageProvider
import ru.nucodelabs.gem.view.control.chart.ImageLineChart
import ru.nucodelabs.geo.map.AzimuthPoint
import ru.nucodelabs.geo.map.Wsg
import java.net.URL
import java.util.*
import javax.inject.Inject

class AnisotropyMainViewController @Inject constructor(
    private val anisotropyMapImageProvider: AnisotropyMapImageProvider
) : AbstractController() {
    @FXML
    private lateinit var satChart: ImageLineChart

    @FXML
    private lateinit var root: VBox
    override val stage: Stage?
        get() = root.scene?.window as Stage?

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        stage?.onShown = EventHandler {
            setupSatelliteChart()
        }
    }

    private fun setupSatelliteChart() {
        val satImg = anisotropyMapImageProvider.satImage(
            center = Wsg(54.568056, 82.654444),
            maxAb2WithAzimuth = listOf(
                AzimuthPoint(117.5, 0.0),
                AzimuthPoint(117.5, 45.0),
                AzimuthPoint(117.5, 90.0),
                AzimuthPoint(117.5, 135.0)
            )
        )
        satChart.image = satImg
    }
}