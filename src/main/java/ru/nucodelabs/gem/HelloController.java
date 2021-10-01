package ru.nucodelabs.gem;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;

import java.util.Objects;

public class HelloController {
    @FXML
    public MenuBar mainMenuBar;

//    @FXML
//    protected boolean MenuBarMacOS() {
//        //            assert mainMenuBar != null;
//        //            mainMenuBar.setUseSystemMenuBar(true);
//        return Objects.equals(System.getProperty("os.name"), "Mac OS X");
//    }

//    mainMenuBar.setUseSystemMenuBar(true);

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("i luv Lerussia!!");
    }
}