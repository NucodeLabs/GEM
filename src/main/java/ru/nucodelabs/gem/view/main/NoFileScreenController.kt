package ru.nucodelabs.gem.view.main

import javafx.beans.property.BooleanProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.ListView
import javafx.scene.control.SelectionMode
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseButton
import javafx.scene.input.TransferMode
import javafx.scene.layout.VBox
import javafx.stage.Stage
import ru.nucodelabs.gem.app.pref.RECENT_FILES
import ru.nucodelabs.gem.util.fx.emptyBinding
import ru.nucodelabs.gem.view.AbstractController
import java.io.File
import java.net.URL
import java.util.*
import java.util.prefs.Preferences
import javax.inject.Inject
import javax.inject.Provider

class NoFileScreenController @Inject constructor(
    private val fileOpenerProvider: Provider<FileOpener>,
    private val fileImporterProvider: Provider<FileImporter>,
    private val preferences: Preferences
) : AbstractController(), FileOpener by fileOpenerProvider.get(), FileImporter by fileImporterProvider.get() {

    @FXML
    private lateinit var recentFiles: ListView<File>

    @FXML
    private lateinit var recentFilesContainer: VBox

    @FXML
    private lateinit var root: VBox

    override val stage: Stage?
        get() = root.scene.window as Stage?

    fun visibleProperty(): BooleanProperty = root.visibleProperty()

    override fun initialize(location: URL, resources: ResourceBundle) {
        recentFiles.selectionModel.selectionMode = SelectionMode.SINGLE

        recentFiles.onMouseClicked = EventHandler { event ->
            if (event.button == MouseButton.PRIMARY) {
                if (recentFiles.selectionModel.selectedItems.size == 1) {
                    openJsonSection(recentFiles.selectionModel.selectedItem)
                }
            }
        }

        recentFilesContainer.visibleProperty().bind(recentFiles.items.emptyBinding().not())
        recentFilesContainer.managedProperty().bind(recentFilesContainer.visibleProperty())

        initConfig(preferences)
        visibleProperty().addListener { _, _, newValue ->
            if (newValue) {
                initConfig(preferences)
            }
        }
    }

    private fun initConfig(preferences: Preferences) {
        val recentFilesPaths = preferences[RECENT_FILES.key, RECENT_FILES.def].split(File.pathSeparator)

        recentFiles.items.setAll(
            recentFilesPaths.dropLastWhile { it.isEmpty() }
                .distinct()
                .map { File(it) }
                .filter { it.exists() }
                .also { existingFiles ->
                    preferences.put(
                        RECENT_FILES.key,
                        existingFiles.joinToString(File.pathSeparator) { file -> file.absolutePath }
                    )
                }
        )
    }

    @FXML
    private fun dragOverHandle(dragEvent: DragEvent) {
        if (dragEvent.dragboard.hasFiles()) {
            val files = dragEvent.dragboard.files
            for (file in files) {
                if (file.name.endsWith(".EXP", ignoreCase = true)
                    || file.name.endsWith(".json", ignoreCase = true)
                ) {
                    dragEvent.acceptTransferModes(*TransferMode.COPY_OR_MOVE)
                }
            }
        }
        dragEvent.consume()
    }

    @FXML
    private fun dragDropHandle(dragEvent: DragEvent) {
        if (dragEvent.dragboard.hasFiles()) {
            val files = dragEvent.dragboard.files
            dragEvent.isDropCompleted = true
            dragEvent.consume()
            for (file in files) {
                if (file.name.endsWith(".exp", ignoreCase = true)) {
                    importEXP(file)
                } else if (file.name.endsWith(".json", ignoreCase = true)) {
                    openJsonSection(file)
                }
            }
        }
    }

    @FXML
    private fun clearRecentFiles() {
        preferences.apply {
            put(RECENT_FILES.key, RECENT_FILES.def)
        }.also {
            initConfig(it)
        }
    }
}