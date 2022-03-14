package ru.nucodelabs.gem.view.alerts;

import javafx.stage.Stage;

public class IncorrectFileAlert extends ExceptionAlert {
    public IncorrectFileAlert(Exception exception) {
        this(exception, null);
    }

    public IncorrectFileAlert(Exception exception, Stage owner) {
        super(exception, owner);
        this.setHeaderText(AlertContent.INCORRECT_FILE);
    }
}
