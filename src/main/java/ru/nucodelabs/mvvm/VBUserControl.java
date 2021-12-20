package ru.nucodelabs.mvvm;

import javafx.scene.layout.VBox;

public abstract class VBUserControl extends VBox {
    public VBUserControl() {
        Initializers.initFXMLControl(this);
    }
}
