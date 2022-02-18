package ru.nucodelabs.mvvm;

import javafx.scene.layout.VBox;

import java.util.ResourceBundle;

/**
 * Abstract class of user control which have VBox as root container.
 * You must create same named FXML-file in same package in resources folder.
 */
public abstract class VBUserControl extends VBox {
    protected ResourceBundle uiProperties;

    public VBUserControl() {
        Initializers.initFXMLControl(this);
    }

    public VBUserControl(ResourceBundle uiProperties) {
        this();
        this.uiProperties = uiProperties;
    }
}
