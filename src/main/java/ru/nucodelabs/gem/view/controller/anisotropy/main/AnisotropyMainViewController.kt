package ru.nucodelabs.gem.view.controller.anisotropy.main

import javafx.beans.binding.Bindings
import javafx.fxml.FXML
import javafx.scene.chart.ScatterChart
import javafx.scene.chart.XYChart.Data
import javafx.scene.chart.XYChart.Series
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import ru.nucodelabs.gem.app.io.saveInitialDirectory
import ru.nucodelabs.gem.app.pref.JSON_FILES_DIR
import ru.nucodelabs.gem.fxmodel.anisotropy.app.AnisotropyFxAppModel
import ru.nucodelabs.geo.map.xFromCenter
import ru.nucodelabs.geo.map.yFromCenter
import ru.nucodelabs.kfx.core.AbstractViewController
import ru.nucodelabs.kfx.ext.toObservableList
import java.io.File
import java.net.URL
import java.util.*
import java.util.prefs.Preferences
import javax.inject.Inject
import javax.inject.Named

class AnisotropyMainViewController @Inject constructor(
    private val appModel: AnisotropyFxAppModel,
    @Named("JSON") private val fileChooser: FileChooser,
    private val preferences: Preferences
) : AbstractViewController<VBox>() {
    @FXML
    lateinit var chart: ScatterChart<Number, Number>

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        val convert = {
            val seriesList =
                appModel.observablePoint.azimuthSignals.map { observableAzimuthSignals ->
                    observableAzimuthSignals.signals.sortedSignals.map { signal ->
                        Data<Number, Number>(
                            xFromCenter(signal.ab2, observableAzimuthSignals.azimuth),
                            yFromCenter(signal.ab2, observableAzimuthSignals.azimuth)
                        )
                    }
                }.map {
                    Series(it.toObservableList())
                }

            seriesList.toObservableList()
        }
        chart.data = convert()
        chart.dataProperty().bind(
            Bindings.createObjectBinding(
                convert,
                appModel.observablePoint.azimuthSignals,
            )
        )
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