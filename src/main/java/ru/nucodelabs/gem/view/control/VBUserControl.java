package ru.nucodelabs.gem.view.control;

import javafx.scene.layout.VBox;
import ru.nucodelabs.gem.util.fx.FXUtils;

/**
 * Abstract class of user control which have VBox as root container.
 * You must create same named FXML-file in same package in resources folder.
 */
public abstract class VBUserControl extends VBox {

    public VBUserControl() {
        FXUtils.initFXMLControl(this);
    }
}
