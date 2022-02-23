package ru.nucodelabs.gem.view.usercontrols.nofile;

import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ru.nucodelabs.gem.view.usercontrols.VBUserControl;

public class NoFileScreen extends VBUserControl {

    @FXML
    private Button openEXPButton;

    public ObjectProperty<EventHandler<ActionEvent>> openEXPOnActionProperty() {
        return openEXPButton.onActionProperty();
    }

    public EventHandler<ActionEvent> getOpenEXPOnAction() {
        return openEXPButton.getOnAction();
    }

    public void setOpenEXPOnAction(EventHandler<ActionEvent> value) {
        openEXPButton.setOnAction(value);
    }

    @FXML
    private Button openSectionButton;

    public ObjectProperty<EventHandler<ActionEvent>> openSectionOnActionProperty() {
        return openSectionButton.onActionProperty();
    }

    public EventHandler<ActionEvent> getOpenSectionOnAction() {
        return openSectionButton.getOnAction();
    }

    public void setOpenSectionOnAction(EventHandler<ActionEvent> value) {
        openSectionButton.setOnAction(value);
    }
}
