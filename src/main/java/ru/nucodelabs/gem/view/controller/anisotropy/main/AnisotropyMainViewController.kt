package ru.nucodelabs.gem.view.controller.anisotropy.main

import javafx.beans.binding.Bindings
import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.util.StringConverter
import ru.nucodelabs.gem.app.io.saveInitialDirectory
import ru.nucodelabs.gem.app.pref.JSON_FILES_DIR
import ru.nucodelabs.gem.config.ArgNames
import ru.nucodelabs.gem.fxmodel.anisotropy.app.AnisotropyFxAppModel
import ru.nucodelabs.gem.view.color.ColorMapper
import ru.nucodelabs.gem.view.control.chart.CombinedChart
import ru.nucodelabs.gem.view.control.chart.NucodeNumberAxis
import ru.nucodelabs.gem.view.control.chart.SmartInterpolationMap
import ru.nucodelabs.gem.view.controller.util.mapToPoints
import ru.nucodelabs.kfx.core.AbstractViewController
import java.io.File
import java.net.URL
import java.util.*
import java.util.prefs.Preferences
import javax.inject.Inject
import javax.inject.Named

const val MAP_IMAGE_SIZE = 350

class AnisotropyMainViewController @Inject constructor(
    private val appModel: AnisotropyFxAppModel,
    @Named(ArgNames.File.JSON) private val fileChooser: FileChooser,
    private val preferences: Preferences,
    private val colorMapper: ColorMapper,
    private val formatter: StringConverter<Number>
) : AbstractViewController<VBox>() {
    @FXML
    private lateinit var signalsMapAxisY: NucodeNumberAxis

    @FXML
    private lateinit var signalsMapAxisX: NucodeNumberAxis

    @FXML
    lateinit var signalsMap: CombinedChart

    @FXML
    lateinit var signalsInterpolation: SmartInterpolationMap

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)

        signalsMapAxisY.tickLabelFormatter = formatter
        signalsMapAxisX.tickLabelFormatter = formatter

        signalsInterpolation.colorMapper = colorMapper
        signalsInterpolation.data = mapToPoints(appModel.observablePoint.azimuthSignals)
        signalsMap.colorMapper = colorMapper
        signalsMap.data = mapToPoints(appModel.observablePoint.azimuthSignals)
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
        appModel.observablePoint.centerProperty().addListener { _, _, _ -> updateSignalsMapImage() }
        appModel.observablePoint.azimuthSignals.addListener(ListChangeListener { updateSignalsMapImage() })
    }

    private fun updateSignalsMapImage() {
        val mapImage = appModel.mapImage(MAP_IMAGE_SIZE)

        if (mapImage != null) {
            signalsMapAxisX.lowerBound = mapImage.xLowerBound
            signalsMapAxisX.upperBound = mapImage.xUpperBound
            signalsMapAxisX.forceMarks.add(0.0)
            signalsMapAxisX.forceMarks.add(0.0)

            signalsMapAxisY.forceMarks.add(0.0)
            signalsMapAxisY.forceMarks.add(0.0)
            signalsMapAxisY.lowerBound = mapImage.yLowerBound
            signalsMapAxisY.upperBound = mapImage.yUpperBound
        }

        signalsMap.image = mapImage?.image
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