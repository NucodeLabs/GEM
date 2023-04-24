package ru.nucodelabs.gem.view.controller.main

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Data
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.stage.Stage
import ru.nucodelabs.gem.fxmodel.anisotropy.app.AnisotropyMapImageProvider
import ru.nucodelabs.gem.util.TextToTableParser
import ru.nucodelabs.gem.util.fx.observableListOf
import ru.nucodelabs.gem.util.fx.toObservableList
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.gem.view.control.chart.ImageScatterChart
import ru.nucodelabs.gem.view.control.chart.NucodeNumberAxis
import ru.nucodelabs.gem.view.control.chart.SmartInterpolationMap
import ru.nucodelabs.gem.view.controller.AbstractController
import ru.nucodelabs.geo.anisotropy.calc.map.*
import java.net.URL
import java.util.*
import javax.inject.Inject

class AnisotropyMainViewController @Inject constructor(
    private val anisotropyMapImageProvider: AnisotropyMapImageProvider,
    private val colorMapper: ColorMapper
) : AbstractController() {
    @FXML
    private lateinit var scaleTf: TextField

    @FXML
    private lateinit var interpolChart: SmartInterpolationMap

    @FXML
    private lateinit var satChart: ImageScatterChart

    @FXML
    private lateinit var root: VBox
    override val stage: Stage?
        get() = root.scene?.window as Stage?

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        interpolChart.colorMapper = colorMapper
        stage?.onShown = EventHandler {
            //setupInterpolationChart()
            //setupSatelliteChart()
        }
    }

    private val points0Deg = """
        36.1003
        40.149
        46.9043
        52.3072
        58.3726
        64.6402
        65.0106
        70.7175
        76.5502
        82.9475
        87.5528
        94.1959
        102.743
        110.356
        116.45
        122.531
        128.153
        133.841
        136.253
        142.164
        148.713
        154.985
        160.309
    """.trimIndent()

    private val point45Deg = """
        35.4168
        38.7863
        44.0232
        49.3817
        55.0438
        61.6917
        67.1276
        74.545
        81.5458
        89.792
        96.8466
        103.065
        110.351
        117.951
        124.808
        130.924
        138.959
        144.877
        149.653
        156.251
        163.365
        169.506
        175.711
    """.trimIndent()

    private val points90Deg = """
        35.0817
        38.9199
        44.601
        49.8457
        56.8688
        63.0365
        70.3447
        77.0616
        83.8659
        90.4042
        97.6685
        104.301
        109.813
        118.347
        125.895
        133.445
        140.442
        147.006
        151.849
        158.689
        166.995
        174.124
        182.135
    """.trimIndent()

    private val points135Deg = """
        33.3641
        38.2825
        42.9564
        48.5885
        54.1513
        59.1015
        66.8814
        72.6344
        78.6034
        85.3051
        92.7319
        97.1842
        102.544
        108.918
        114.975
        122.101
        128.738
        135.686
        141.995
        148.302
        155.437
        160.641
        167.452
    """.trimIndent()

    private val ab2Str = """
        7.5
        12.5
        17.5
        22.5
        27.5
        32.5
        37.5
        42.5
        47.5
        52.5
        57.5
        62.5
        67.5
        72.5
        77.5
        82.5
        87.5
        92.5
        97.5
        102.5
        107.5
        112.5
        117.5
    """.trimIndent()

    val parse = { str: String ->
        TextToTableParser(str).parsedTable.map { it.first()!!.toDouble() }
    }
    val ab2 = parse(ab2Str)
    val rho0 = parse(points0Deg)
    val rho45 = parse(point45Deg)
    val rho90 = parse(points90Deg)
    val rho135 = parse(points135Deg)

    val points0 = List(ab2.size) {
        Data<Number, Number>(
            xFromCenter(ab2[it], 0.0),
            yFromCenter(ab2[it], 0.0),
            rho0[it]
        )
    } + List(ab2.size) {
        Data<Number, Number>(
            xFromCenter(ab2[it], 180.0),
            yFromCenter(ab2[it], 180.0),
            rho0[it]
        )
    }

    val points45 = List(ab2.size) {
        Data<Number, Number>(
            xFromCenter(ab2[it], 45.0),
            yFromCenter(ab2[it], 45.0),
            rho45[it]
        )
    } + List(ab2.size) {
        Data<Number, Number>(
            xFromCenter(ab2[it], 45 + 180.0),
            yFromCenter(ab2[it], 45 + 180.0),
            rho45[it]
        )
    }

    val points90 = List(ab2.size) {
        Data<Number, Number>(
            xFromCenter(ab2[it], 90.0),
            yFromCenter(ab2[it], 90.0),
            rho90[it]
        )
    } + List(ab2.size) {
        Data<Number, Number>(
            xFromCenter(ab2[it], 90 + 180.0),
            yFromCenter(ab2[it], 90 + 180.0),
            rho90[it]
        )
    }

    val points135 = List(ab2.size) {
        Data<Number, Number>(
            xFromCenter(ab2[it], 135.0),
            yFromCenter(ab2[it], 135.0),
            rho135[it]
        )
    } + List(ab2.size) {
        Data<Number, Number>(
            xFromCenter(ab2[it], 135 + 180.0),
            yFromCenter(ab2[it], 135 + 180.0),
            rho135[it]
        )
    }

    private fun chartPointsData() = observableListOf(
        XYChart.Series(
            (copy(points0) + copy(points45) + copy(points90) + copy(points135)).toObservableList()
        )
    )

    private fun copy(list: List<Data<Number, Number>>): javafx.collections.ObservableList<Data<Number, Number>> {
        return list.map { Data(it.xValue, it.yValue, it.extraValue) }.toObservableList()
    }

    private fun setupInterpolationChart() {
        interpolChart.data = chartPointsData()
    }

/*    private fun setupSatelliteChart(scale: Double = 1.0) {
        val center = Wgs(82.654444, 54.568056)
        val maxAb2WithAzimuth = listOf(
            AzimuthPoint(117.5, 0.0),
            AzimuthPoint(117.5, 45.0),
            AzimuthPoint(117.5, 90.0),
            AzimuthPoint(117.5, 135.0)
        )
        val mapSizer = MapSizer(center, maxAb2WithAzimuth, scale)
        val satImg = anisotropyMapImageProvider.satImage(mapSizer)

        (satChart.xAxis as NucodeNumberAxis).lowerBound = -mapSizer.maxAbsXFromCenterScaledInMeters
        (satChart.xAxis as NucodeNumberAxis).upperBound = +mapSizer.maxAbsXFromCenterScaledInMeters
        (satChart.yAxis as NucodeNumberAxis).lowerBound = -mapSizer.maxAbsYFromCenterScaledInMeters
        (satChart.yAxis as NucodeNumberAxis).upperBound = +mapSizer.maxAbsYFromCenterScaledInMeters

        satChart.image = satImg.image
        satChart.data = chartPointsData()
    }*/

/*    @FXML
    private fun applyScale() {
        setupSatelliteChart(scaleTf.text.toDoubleOrNull() ?: 1.0)
    }*/
}