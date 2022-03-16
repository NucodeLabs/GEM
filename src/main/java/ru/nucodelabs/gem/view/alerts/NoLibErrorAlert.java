package ru.nucodelabs.gem.view.alerts;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class NoLibErrorAlert extends Alert {
    public NoLibErrorAlert(UnsatisfiedLinkError error) {
        super(AlertType.ERROR, error.getMessage());
        this.setTitle(AlertContent.NO_LIB);
        this.setHeaderText(AlertContent.UNABLE_TO_DRAW_CHART);
    }

    public NoLibErrorAlert(UnsatisfiedLinkError error, Stage owner) {
        this(error);
        this.initOwner(owner);
    }
}
