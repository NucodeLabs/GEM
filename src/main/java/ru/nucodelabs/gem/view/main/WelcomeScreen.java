package ru.nucodelabs.gem.view.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ru.nucodelabs.mvvm.VBUserControl;

public class WelcomeScreen extends VBUserControl {
    @FXML
    private Button openEXPButton;

    public Button getOpenEXPButton() {
        return openEXPButton;
    }
}
