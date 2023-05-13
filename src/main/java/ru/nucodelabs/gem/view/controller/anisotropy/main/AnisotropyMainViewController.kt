package ru.nucodelabs.gem.view.controller.anisotropy.main

import javafx.beans.binding.Bindings
import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import ru.nucodelabs.gem.app.io.saveInitialDirectory
import ru.nucodelabs.gem.app.pref.JSON_FILES_DIR
import ru.nucodelabs.gem.config.ArgNames
import ru.nucodelabs.gem.fxmodel.anisotropy.app.AnisotropyFxAppModel
import ru.nucodelabs.gem.util.std.toDoubleOrNullBy
import ru.nucodelabs.gem.view.AlertsFactory
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.gem.view.control.chart.ImageScatterChart
import ru.nucodelabs.gem.view.control.chart.SmartInterpolationMap
import ru.nucodelabs.gem.view.controller.util.mapToPoints
import ru.nucodelabs.kfx.core.AbstractViewController
import java.io.File
import java.net.URL
import java.text.DecimalFormat
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
    @Named(ArgNames.PRECISE) private val preciseDecimalFormat: DecimalFormat,
    private val alertsFactory: AlertsFactory
) : AbstractViewController<VBox>() {

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
        appModel.observablePoint.centerProperty().addListener { _, _, _ ->
            updateSignalsMapImage()
            updatePointCenterTextFields()
        }
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
                appModel.editCenter(newLatitude, newLongitude)
            } catch (e: Exception) {
                alertsFactory.simpleExceptionAlert(e).show()
            }
        }
    }
}