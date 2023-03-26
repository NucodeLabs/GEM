package ru.nucodelabs.gem.view.controller.anisotropy.main

import javafx.fxml.FXML
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import ru.nucodelabs.gem.app.io.saveInitialDirectory
import ru.nucodelabs.gem.app.pref.JSON_FILES_DIR
import ru.nucodelabs.gem.fxmodel.anisotropy.app.AnisotropyFxAppModel
import ru.nucodelabs.kfx.core.AbstractViewController
import java.io.File
import java.util.prefs.Preferences
import javax.inject.Inject
import javax.inject.Named

class AnisotropyMainViewController @Inject constructor(
    private val appModel: AnisotropyFxAppModel,
    @Named("JSON") private val fileChooser: FileChooser,
    private val preferences: Preferences
) : AbstractViewController<VBox>() {
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