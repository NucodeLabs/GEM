package ru.nucodelabs.gem.view.alerts;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class UnsafeDataAlert extends Alert {
    public UnsafeDataAlert(String dataName) {
        this(dataName, null);
    }

    public UnsafeDataAlert(String dataName, Stage owner) {
        super(AlertType.WARNING);
        this.setTitle(AlertContent.COMPATIBILITY_MODE);
        this.setHeaderText(dataName + " - " + AlertContent.EXP_STT_MISMATCH);
        this.setContentText(AlertContent.MINIMAL_DATA);
        this.initOwner(owner);
    }
}
