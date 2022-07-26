package ru.nucodelabs.gem.view.charts

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.layout.VBox
import javafx.stage.Stage
import ru.nucodelabs.gem.extensions.fx.bindTo
import ru.nucodelabs.gem.view.AbstractController
import java.net.URL
import java.util.*

class ModelSectionSwitcherController : AbstractController() {
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

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        logSectionBox.managedProperty() bindTo logSectionBox.visibleProperty()
        linearSectionBox.managedProperty() bindTo linearSectionBox.visibleProperty()

        logSectionBox.isVisible = false
        logSectionBox.visibleProperty() bindTo linearSectionBox.visibleProperty().not()
        linearSectionBox.isVisible = true

        logSectionBox.apply {
            val contextMenu = ContextMenu(
                MenuItem("Переключить на линейный масштаб").apply {
                    onAction = EventHandler { linearSectionBox.isVisible = true }
                }
            )
            onContextMenuRequested = EventHandler { contextMenu.show(logSectionBox, it.screenX, it.screenY) }
        }

        linearSectionBox.apply {
            val contextMenu = ContextMenu(
                MenuItem("Переключить на псевдо-логарифмический масштаб").apply {
                    onAction = EventHandler { linearSectionBox.isVisible = false }
                }
            )
            onContextMenuRequested = EventHandler { contextMenu.show(linearSectionBox, it.screenX, it.screenY) }
        }
    }
}