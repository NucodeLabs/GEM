package ru.nucodelabs.gem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import ru.nucodelabs.gem.Files;

import java.io.File;

public class AppViewController {

    @FXML
    public Button inverseButton;
    @FXML
    public MenuItem menuFileOpen;
    @FXML
    public BorderPane mainPane;

    @FXML
    public void onMenuFileOpen() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose STT file");
        File file = chooser.showOpenDialog(mainPane.getScene().getWindow());
    }


}
