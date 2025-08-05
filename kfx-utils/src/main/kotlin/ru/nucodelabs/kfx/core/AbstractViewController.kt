package ru.nucodelabs.kfx.core

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.stage.Stage
import java.net.URL
import java.util.*

/**
 * Abstract JavaFX controller which already implements `Initializable`.
 * It runs fxScriptInit which can be defined in related FXML file using `fx:script` tag.
 * To use this controller you have to set `fx:id` attribute to `"root"` in root node of corresponding FXML-view.
 * Then you can obtain root node via property `root` and you can try to obtain stage via `stage` property.
 * @param N type of root node
 */
abstract class AbstractViewController<N : Node> : Initializable {

    @FXML
    protected var fxScriptInit: Runnable = Runnable {}

    /**
     * Root node FXML-injected
     */
    @FXML
    protected lateinit var root: N

    /**
     * Current window
     */
    protected val stage: Stage?
        get() = root.scene?.window as Stage?


    override fun initialize(location: URL, resources: ResourceBundle) {
        fxScriptInit()
    }

    private fun fxScriptInit() = fxScriptInit.run()
}