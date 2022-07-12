package ru.nucodelabs.gem.view.control;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class Placeholder extends VBUserControl {
    @FXML
    private Label text;

    public String getText() {
        return text.textProperty().get();
    }

    public void setText(String text) {
        this.text.textProperty().set(text);
    }

    public StringProperty textProperty() {
        return text.textProperty();
    }
}
