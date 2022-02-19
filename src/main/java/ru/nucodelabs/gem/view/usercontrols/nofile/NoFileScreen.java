package ru.nucodelabs.gem.view.usercontrols.nofile;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ru.nucodelabs.mvvm.VBUserControl;

public class NoFileScreen extends VBUserControl {
    @FXML
    private Button openEXPButton;

    public Button getOpenEXPButton() {
        return openEXPButton;
    }
}
