package ru.nucodelabs.gem.view.alerts;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class ExceptionAlert extends Alert {
    public ExceptionAlert(Exception exception) {
        super(AlertType.ERROR, exception.getMessage());
    }

    public ExceptionAlert(Exception exception, Stage owner) {
        this(exception);
        this.initOwner(owner);
    }
}
