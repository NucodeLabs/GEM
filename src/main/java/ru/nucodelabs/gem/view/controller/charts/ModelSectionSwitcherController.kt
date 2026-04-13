package ru.nucodelabs.gem.view.controller.charts

import jakarta.inject.Inject
import jakarta.inject.Named
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import ru.nucodelabs.gem.app.pref.PNG_FILES_DIR
import ru.nucodelabs.gem.config.Name
import ru.nucodelabs.kfx.core.AbstractViewController
import ru.nucodelabs.kfx.ext.hide
import ru.nucodelabs.kfx.ext.saveSnapshotAsPng
import ru.nucodelabs.kfx.ext.switch
import java.net.URL
import java.util.*
import java.util.prefs.Preferences

class ModelSectionSwitcherController @Inject constructor(
    @Named(Name.File.PNG) private val fc: FileChooser,
    private val prefs: Preferences
) : AbstractViewController<VBox>() {
    @FXML
    private lateinit var logSectionBox: VBox

    @FXML
    private lateinit var linearSectionBox: VBox

    @FXML
    lateinit var logSectionBoxController: ModelSectionController

    @FXML
    lateinit var linearSectionBoxController: ModelSectionController

    override fun initialize(location: URL, resources: ResourceBundle) {
        logSectionBoxController.chart.extraValueVisibleProperty()
            .bindBidirectional(linearSectionBoxController.chart.extraValueVisibleProperty())

        hide(logSectionBox)
        logSectionBox.apply {
            val contextMenu = ContextMenu(
                MenuItem("Переключить на линейный масштаб").apply {
                    onAction = EventHandler { switch(logSectionBox, linearSectionBox) }
                },
                CheckMenuItem("Показывать сопротивления").apply {
                    isSelected = false
                    selectedProperty()
                        .bindBidirectional(logSectionBoxController.chart.extraValueVisibleProperty())
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
                    onAction = EventHandler { switch(linearSectionBox, logSectionBox) }
                },
                CheckMenuItem("Показывать сопротивления").apply {
                    isSelected = false
                    selectedProperty()
                        .bindBidirectional(linearSectionBoxController.chart.extraValueVisibleProperty())
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