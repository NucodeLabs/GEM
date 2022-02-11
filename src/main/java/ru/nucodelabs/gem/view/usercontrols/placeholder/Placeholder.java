package ru.nucodelabs.gem.view.usercontrols.placeholder;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import ru.nucodelabs.mvvm.VBUserControl;

public class Placeholder extends VBUserControl {
    @FXML
    Text text;

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
