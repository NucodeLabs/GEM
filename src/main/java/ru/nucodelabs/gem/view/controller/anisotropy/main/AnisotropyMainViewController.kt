package ru.nucodelabs.gem.view.controller.anisotropy.main

import javafx.beans.binding.Bindings
import javafx.fxml.FXML
import javafx.scene.control.Tooltip
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import ru.nucodelabs.gem.app.io.saveInitialDirectory
import ru.nucodelabs.gem.app.pref.JSON_FILES_DIR
import ru.nucodelabs.gem.fxmodel.anisotropy.app.AnisotropyFxAppModel
import ru.nucodelabs.gem.fxmodel.map.MapImageData
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.gem.view.control.chart.ImageScatterChart
import ru.nucodelabs.gem.view.control.chart.SmartInterpolationMap
import ru.nucodelabs.gem.view.control.chart.installTooltips
import ru.nucodelabs.gem.view.controller.anisotropy.main.map.toPoints
import ru.nucodelabs.kfx.core.AbstractViewController
import ru.nucodelabs.kfx.ext.forCharts
import java.io.File
import java.net.URL
import java.util.*
import java.util.prefs.Preferences
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.round

class AnisotropyMainViewController @Inject constructor(
    private val appModel: AnisotropyFxAppModel,
    @Named("JSON") private val fileChooser: FileChooser,
    private val preferences: Preferences,
    private val colorMapper: ColorMapper,
) : AbstractViewController<VBox>() {
    @FXML
    lateinit var mapChart: ImageScatterChart

    @FXML
    lateinit var chart: SmartInterpolationMap

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        chart.colorMapper = colorMapper
        chart.data = toPoints(appModel.observablePoint.azimuthSignals)
        chart.dataProperty().bind(
            Bindings.createObjectBinding(
                { toPoints(appModel.observablePoint.azimuthSignals) },
                appModel.observablePoint.azimuthSignals,
            )
        )

        mapChart.dataProperty().bind(
            Bindings.createObjectBinding(
                { toPoints(appModel.observablePoint.azimuthSignals) },
                appModel.observablePoint.azimuthSignals,
            )
        )
        mapChart.imageProperty().bind(
            Bindings.createObjectBinding(
                { appModel.mapImage()?.image },
                appModel.observablePoint.centerProperty()
            )
        )
    }

    @FXML
    fun loadProject() {
        val file: File? = fileChooser.showOpenDialog(stage)
        if (file != null) {
            saveInitialDirectory(preferences, JSON_FILES_DIR, fileChooser, file)
            appModel.loadProject(file)

            val mapImage: MapImageData? = appModel.mapImage()

            if (mapImage != null) {
                mapChart.setAxisRange(
                    round(mapImage.xLowerBound),
                    round(mapImage.xUpperBound),
                    round(mapImage.yLowerBound),
                    round(mapImage.yUpperBound)
                )
                mapChart.installTooltips { seriesIndex, series, pointIndex, _ ->
                    Tooltip(
                        """
                            ${appModel.observablePoint.azimuthSignals[0]
                                .signals.sortedSignals[pointIndex / 2].ab2}
                        """.trimIndent()
                    ).forCharts()
                }
            }
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