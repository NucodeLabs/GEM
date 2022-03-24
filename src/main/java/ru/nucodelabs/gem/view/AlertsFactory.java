package ru.nucodelabs.gem.view;

import jakarta.validation.ConstraintViolation;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class AlertsFactory {
    private final ResourceBundle uiProperties;

    @Inject
    public AlertsFactory(ResourceBundle uiProperties) {
        this.uiProperties = uiProperties;
    }

    public Alert simpleExceptionAlert(Exception e) {
        return new Alert(Alert.AlertType.ERROR, e.getMessage());
    }

    public Alert simpleExceptionAlert(Exception e, Stage owner) {
        Alert alert = simpleExceptionAlert(e);
        alert.initOwner(owner);
        return alert;
    }

    public Alert unsatisfiedLinkErrorAlert(UnsatisfiedLinkError e, Stage owner) {
        Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
        alert.setTitle(uiProperties.getString("noLib"));
        alert.setHeaderText(uiProperties.getString("unableToDrawChart"));
        alert.initOwner(owner);
        return alert;
    }

    public Alert incorrectFileAlert(Exception e, Stage owner) {
        Alert alert = simpleExceptionAlert(e);
        alert.setHeaderText(uiProperties.getString("fileError"));
        alert.initOwner(owner);
        return alert;
    }

    public Alert unsafeDataAlert(String picketName, Stage owner) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(uiProperties.getString("compatibilityMode"));
        alert.setHeaderText(picketName + " - " + uiProperties.getString("EXPSTTMismatch"));
        alert.setContentText(uiProperties.getString("minimalDataWillBeDisplayed"));
        alert.initOwner(owner);
        return alert;
    }

    public <T> Alert violationsAlert(Set<ConstraintViolation<T>> violations, Stage owner) {
        String message = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("\n"));
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.initOwner(owner);
        return alert;
    }
}
