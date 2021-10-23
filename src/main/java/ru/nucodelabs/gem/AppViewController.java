package ru.nucodelabs.gem;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import ru.nucodelabs.files.sonet.STTFile;

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
        chooser.setTitle("Choose files for interpretation");
//        example code below

//        chooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("EXP - Экспериментальные данные", "*.EXP", "*.exp"),
//                new FileChooser.ExtensionFilter("STT - Установки", "*.STT", "*.stt"),
//                new FileChooser.ExtensionFilter("MOD - Модели", "*.MOD", "*.mod")
//                );
        File file = chooser.showOpenDialog(mainPane.getScene().getWindow());
        STTFile inp = new STTFile();
    }


}
