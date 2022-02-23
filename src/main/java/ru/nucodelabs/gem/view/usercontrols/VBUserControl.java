package ru.nucodelabs.gem.view.usercontrols;

import javafx.scene.layout.VBox;
import ru.nucodelabs.gem.core.utils.Initializers;

/**
 * Abstract class of user control which have VBox as root container.
 * You must create same named FXML-file in same package in resources folder.
 */
public abstract class VBUserControl extends VBox {

    public VBUserControl() {
        Initializers.initFXMLControl(this);
    }
}
