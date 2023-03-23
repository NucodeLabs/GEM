package ru.nucodelabs.gem.view.charts

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import ru.nucodelabs.gem.app.pref.PNG_FILES_DIR
import ru.nucodelabs.gem.util.fx.bindTo
import ru.nucodelabs.gem.util.fx.saveSnapshotAsPng
import ru.nucodelabs.gem.view.AbstractController
import java.net.URL
import java.util.*
import java.util.prefs.Preferences
import javax.inject.Inject
import javax.inject.Named

class ModelSectionSwitcherController @Inject constructor(
    @Named("PNG") private val fc: FileChooser,
    private val prefs: Preferences
) : AbstractController() {
    @FXML
    private lateinit var logSectionBox: VBox

    @FXML
    private lateinit var linearSectionBox: VBox

    @FXML
    lateinit var logSectionBoxController: ModelSectionController

    @FXML
    lateinit var linearSectionBoxController: ModelSectionController



    override val stage: Stage?
        get() = linearSectionBox.scene?.window as Stage?

    override fun initialize(location: URL, resources: ResourceBundle) {

        logSectionBox.managedProperty() bindTo logSectionBox.visibleProperty()
        linearSectionBox.managedProperty() bindTo linearSectionBox.visibleProperty()

        logSectionBox.isVisible = false
        logSectionBox.visibleProperty() bindTo linearSectionBox.visibleProperty().not()
        linearSectionBox.isVisible = true

        logSectionBox.apply {
            val contextMenu = ContextMenu(
                MenuItem("Переключить на линейный масштаб").apply {
                    onAction = EventHandler { linearSectionBox.isVisible = true }
                },
                MenuItem("Включить надписи").apply {
                    onAction = EventHandler {logSectionBoxController.setupNames(true) }
                },
                MenuItem("Выключить надписи").apply {
                    onAction = EventHandler { logSectionBoxController.setupNames(false) }
                },
                MenuItem("Сохранить как изображение").apply {
                    onAction = EventHandler {
                        logSectionBox.saveSnapshotAsPng(fc)?.also {
                            if (it.parentFile.isDirectory) {
                                fc.initialDirectory = it.parentFile
                                prefs.put(PNG_FILES_DIR.key, it.parentFile.absolutePath)
                            }
                        }
                    }
                }
            )
            onContextMenuRequested = EventHandler { contextMenu.show(logSectionBox, it.screenX, it.screenY) }
        }

        linearSectionBox.apply {
            val contextMenu = ContextMenu(
                MenuItem("Переключить на псевдо-логарифмический масштаб").apply {
                    onAction = EventHandler { linearSectionBox.isVisible = false }
                },
                MenuItem("Включить надписи").apply {
                    onAction = EventHandler { linearSectionBoxController.setupNames(true)}
                },
                MenuItem("Выключить надписи").apply {
                    onAction = EventHandler { linearSectionBoxController.setupNames(false) }
                },
                MenuItem("Сохранить как изображение").apply {
                    onAction = EventHandler {
                        linearSectionBox.saveSnapshotAsPng(fc)?.also {
                            if (it.parentFile.isDirectory) {
                                fc.initialDirectory = it.parentFile
                                prefs.put(PNG_FILES_DIR.key, it.parentFile.absolutePath)
                            }
                        }
                    }
                }
            )
            onContextMenuRequested = EventHandler { contextMenu.show(linearSectionBox, it.screenX, it.screenY) }
        }
    }
}