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

class PseudoSectionSwitcherController : AbstractController() {
    @FXML
    private lateinit var mapPseudoSectionBox: VBox

    @FXML
    private lateinit var curvesPseudoSectionBox: VBox

    @FXML
    lateinit var mapPseudoSectionBoxController: MapPseudoSectionController

    @FXML
    lateinit var curvesPseudoSectionBoxController: CurvesPseudoSectionController

    override val stage: Stage?
        get() = mapPseudoSectionBox.scene?.window as Stage?

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        mapPseudoSectionBox.managedProperty() bindTo mapPseudoSectionBox.visibleProperty()
        curvesPseudoSectionBox.managedProperty() bindTo curvesPseudoSectionBox.visibleProperty()

        mapPseudoSectionBox.visibleProperty() bindTo curvesPseudoSectionBox.visibleProperty().not()
        curvesPseudoSectionBox.isVisible = false

        mapPseudoSectionBox.apply {
            val contextMenu = ContextMenu(
                MenuItem("Переключить на отображение кривыми").apply {
                    onAction = EventHandler { curvesPseudoSectionBox.isVisible = true }
                }
            )
            onContextMenuRequested = EventHandler { contextMenu.show(mapPseudoSectionBox, it.screenX, it.screenY) }
        }

        curvesPseudoSectionBox.apply {
            val contextMenu = ContextMenu(
                MenuItem("Переключить на отображение цветовой картой").apply {
                    onAction = EventHandler { curvesPseudoSectionBox.isVisible = false }
                }
            )
            onContextMenuRequested = EventHandler { contextMenu.show(mapPseudoSectionBox, it.screenX, it.screenY) }
        }
    }
}