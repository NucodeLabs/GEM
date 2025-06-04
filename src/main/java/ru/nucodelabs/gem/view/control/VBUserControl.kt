package ru.nucodelabs.gem.view.control

import javafx.scene.layout.VBox
import ru.nucodelabs.gem.util.fx.FXUtils

/**
 * Abstract user control which has VBox as a root container.
 * The FXML file must have the same name and be located in the same package.
 */
abstract class VBUserControl : VBox() {
    init {
        FXUtils.initFXMLControl(this)
    }
}
