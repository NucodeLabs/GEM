package ru.nucodelabs.gem.view.controller.anisotropy.main

import javafx.beans.binding.Bindings
import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.scene.chart.XYChart
import javafx.scene.control.Tooltip
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import ru.nucodelabs.gem.app.io.saveInitialDirectory
import ru.nucodelabs.gem.app.pref.JSON_FILES_DIR
import ru.nucodelabs.gem.config.ArgNames
import ru.nucodelabs.gem.fxmodel.anisotropy.app.AnisotropyFxAppModel
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.gem.view.control.chart.ImageScatterChart
import ru.nucodelabs.gem.view.control.chart.SmartInterpolationMap
import ru.nucodelabs.gem.view.control.chart.installTooltips
import ru.nucodelabs.gem.view.controller.util.mapToPoints
import ru.nucodelabs.kfx.core.AbstractViewController
import ru.nucodelabs.kfx.ext.forCharts
import java.io.File
import java.net.URL
import java.util.*
import java.util.prefs.Preferences
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.round

const val MAP_IMAGE_SIZE = 350

class AnisotropyMainViewController @Inject constructor(
    private val appModel: AnisotropyFxAppModel,
    @Named(ArgNames.File.JSON) private val fileChooser: FileChooser,
    private val preferences: Preferences,
    private val colorMapper: ColorMapper,
) : AbstractViewController<VBox>() {
    @FXML
    lateinit var signalsMap: ImageScatterChart

    @FXML
    lateinit var signalsInterpolation: SmartInterpolationMap

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        signalsInterpolation.colorMapper = colorMapper
        signalsInterpolation.data = mapToPoints(appModel.observablePoint.azimuthSignals)
        setupListeners()
    }

    private fun setupListeners() {
        signalsInterpolation.dataProperty().bind(
            Bindings.createObjectBinding(
                { mapToPoints(appModel.observablePoint.azimuthSignals) },
                appModel.observablePoint.azimuthSignals,
            )
        )
        signalsMap.dataProperty().bind(
            Bindings.createObjectBinding(
                { mapToPoints(appModel.observablePoint.azimuthSignals) },
                appModel.observablePoint.azimuthSignals,
            )
        )

        appModel.observablePoint.azimuthSignals.addListener(ListChangeListener {
            signalsMap.installTooltips(::tooltipFactory)
        })

        appModel.observablePoint.centerProperty().addListener { _, _, _ -> updateSignalsMapImage() }
        appModel.observablePoint.azimuthSignals.addListener(ListChangeListener { updateSignalsMapImage() })
    }

    private fun updateSignalsMapImage() {
        val mapImage = appModel.mapImage(MAP_IMAGE_SIZE)

        if (mapImage != null) {
            signalsMap.setAxisRange(
                round(mapImage.xLowerBound),
                round(mapImage.xUpperBound),
                round(mapImage.yLowerBound),
                round(mapImage.yUpperBound)
            )
        }

        signalsMap.image = mapImage?.image
    }

    @Suppress("UNUSED_PARAMETER")
    private fun tooltipFactory(seriesIndex: Int, series: XYChart.Series<Number, Number>, pointIndex: Int, point: XYChart.Data<Number, Number>): Tooltip {
        val azimuthSignal = appModel.observablePoint.azimuthSignals[0].signals.sortedSignals[pointIndex / 2].ab2
        val resistance = appModel.observablePoint.azimuthSignals[seriesIndex].signals.effectiveSignals[pointIndex / 2].resistanceApparent
        val azimuth: Double = if (pointIndex % 2 == 0) {
            appModel.observablePoint.azimuthSignals[seriesIndex].azimuth
        } else {
            appModel.observablePoint.azimuthSignals[seriesIndex].azimuth + 180
        }
        val tooltipText = """
            AB/2[m]: $azimuthSignal
            Сопротивление ρₐ[Ω‧m]: $resistance
            Азимут[°]: $azimuth
        """.trimIndent()
        return Tooltip(tooltipText).apply { forCharts() }
    }


    @FXML
    fun loadProject() {
        val file: File? = fileChooser.showOpenDialog(stage)
        if (file != null) {
            saveInitialDirectory(preferences, JSON_FILES_DIR, fileChooser, file)
            appModel.loadProject(file)
        }
    }

    @FXML
    fun saveProject() {
        val file: File? = fileChooser.showSaveDialog(stage)
        if (file != null) {
            saveInitialDirectory(preferences, JSON_FILES_DIR, fileChooser, file)
            appModel.saveProject(file)
        }
    }
}