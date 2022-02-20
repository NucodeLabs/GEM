package ru.nucodelabs.gem.view.usercontrols.nofile;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ru.nucodelabs.mvvm.VBUserControl;

public class NoFileScreen extends VBUserControl {
    @FXML
    private Button openEXPButton;
    @FXML
    private Button openSectionButton;

    public Button getOpenEXPButton() {
        return openEXPButton;
    }

    public Button getOpenSectionButton() {
        return openSectionButton;
    }
}
