package ru.nucodelabs.gem.view.controller.anisotropy.main

import javafx.beans.binding.Bindings
import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.chart.XYChart
import javafx.scene.control.Tooltip
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import ru.nucodelabs.gem.app.io.saveInitialDirectory
import ru.nucodelabs.gem.app.pref.JSON_FILES_DIR
import ru.nucodelabs.gem.config.ArgNames
import ru.nucodelabs.gem.fxmodel.anisotropy.app.AnisotropyFxAppModel
import ru.nucodelabs.gem.fxmodel.anisotropy.app.MapOverlayType
import ru.nucodelabs.gem.fxmodel.map.ObservableWgs
import ru.nucodelabs.gem.util.std.toDoubleOrNullBy
import ru.nucodelabs.gem.view.AlertsFactory
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.gem.view.control.chart.ImageScatterChart
import ru.nucodelabs.gem.view.control.chart.SmartInterpolationMap
import ru.nucodelabs.gem.view.control.chart.installTooltips
import ru.nucodelabs.gem.view.controller.util.mapToPoints
import ru.nucodelabs.kfx.core.AbstractViewController
import ru.nucodelabs.kfx.ext.forCharts
import java.io.File
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import java.util.prefs.Preferences
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.round

private const val MAP_IMAGE_SIZE = 350

class AnisotropyMainViewController @Inject constructor(
    private val appModel: AnisotropyFxAppModel,
    @Named(ArgNames.File.JSON) private val fileChooser: FileChooser,
    private val preferences: Preferences,
    private val colorMapper: ColorMapper,
    @Named(ArgNames.PRECISE) private val preciseDecimalFormat: DecimalFormat,
    private val alertsFactory: AlertsFactory
) : AbstractViewController<VBox>() {

    @FXML
    private lateinit var mapOverlayType: ComboBox<MapOverlayType>

    @FXML
    private lateinit var centerLongitudeTf: TextField

    @FXML
    private lateinit var centerLatitudeTf: TextField

    @FXML
    private lateinit var signalsMap: ImageScatterChart

    @FXML
    private lateinit var signalsInterpolation: SmartInterpolationMap

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        initControls()
        initAndSetupListeners()
    }

    private fun initControls() {
        signalsInterpolation.colorMapper = colorMapper
        mapOverlayType.items += MapOverlayType.values()
        mapOverlayType.selectionModel.select(MapOverlayType.NONE)
    }

    private fun initAndSetupListeners() {
        signalsInterpolation.data = mapToPoints(appModel.observablePoint.azimuthSignals)
        signalsMap.data = mapToPoints(appModel.observablePoint.azimuthSignals)

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
        updatePointCenterTextFields()
        updateSignalsMapImage()
        appModel.observablePoint.centerProperty().addListener { _, _, _ ->
            updateSignalsMapImage()
            updatePointCenterTextFields()
        }
        signalsMap.installTooltips(::tooltipFactory)

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

    private fun updatePointCenterTextFields() {
        centerLatitudeTf.text = preciseDecimalFormat.format(appModel.observablePoint.center?.latitudeInDegrees ?: 0)
        centerLongitudeTf.text = preciseDecimalFormat.format(appModel.observablePoint.center?.longitudeInDegrees ?: 0)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun tooltipFactory(
        seriesIndex: Int,
        series: XYChart.Series<Number, Number>,
        pointIndex: Int,
        point: XYChart.Data<Number, Number>
    ): Tooltip {
        val azimuthSignal = appModel.observablePoint.azimuthSignals[0].signals.sortedSignals[pointIndex / 2].ab2
        val resistance =
            appModel.observablePoint.azimuthSignals[seriesIndex].signals.effectiveSignals[pointIndex / 2].resistanceApparent
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
    private fun loadProject() {
        val file: File? = fileChooser.showOpenDialog(stage)
        if (file != null) {
            saveInitialDirectory(preferences, JSON_FILES_DIR, fileChooser, file)
            appModel.loadProject(file)
        }
    }

    @FXML
    private fun saveProject() {
        val file: File? = fileChooser.showSaveDialog(stage)
        if (file != null) {
            saveInitialDirectory(preferences, JSON_FILES_DIR, fileChooser, file)
            appModel.saveProject(file)
        }
    }

    @FXML
    private fun modifyPointCenter() {
        val newLatitude = centerLatitudeTf.text.toDoubleOrNullBy(preciseDecimalFormat)
        val newLongitude = centerLongitudeTf.text.toDoubleOrNullBy(preciseDecimalFormat)

        if (newLatitude == null || newLongitude == null) {
            alertsFactory.simpleAlert(text = "Введите вещественные числа").show()
        }

        if (newLatitude != null && newLongitude != null) {
            try {
                appModel.editCenter(ObservableWgs(newLongitude, newLatitude))
            } catch (e: Exception) {
                alertsFactory.simpleExceptionAlert(e).show()
            }
        }
    }

    @FXML
    private fun undo() {
        appModel.undo()
    }

    @FXML
    private fun redo() {
        appModel.redo()
    }

    @FXML
    private fun newProject() {
        appModel.newProject()
    }
}