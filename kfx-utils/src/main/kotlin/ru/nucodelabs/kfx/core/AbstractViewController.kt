package ru.nucodelabs.kfx.core

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.stage.Stage

/**
 * Abstract JavaFX controller which already implements `Initializable`.
 * To use this controller you have to set `fx:id` attribute to `"root"` in root node of corresponding FXML-view.
 * Then you can obtain root node via property `root` and you can try to obtain stage via `stage` property.
 * @param N type of root node
 */
abstract class AbstractViewController<N : Node> : Initializable {

    /**
     * Root node FXML-injected
     */
    @FXML
    protected lateinit var root: N

    /**
     * Current window
     */
    @Deprecated("Use stage() method", replaceWith = ReplaceWith("rootStage()"))
    protected val stage: Stage?
        get() = root.scene?.window as Stage?

    /**
     * Window that contains the controller root node
     */
    protected fun rootStage(): Stage? = root.scene?.window as? Stage
}