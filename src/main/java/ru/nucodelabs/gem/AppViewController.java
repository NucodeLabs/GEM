package ru.nucodelabs.gem;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.STTFile;
import ru.nucodelabs.files.sonet.Sonet;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;

public class AppViewController {

    @FXML
    public VBox mainPane;
    public MenuItem menuFileOpenSTT;
    public TitledPane vesPane;

    public void onMenuFileOpenSTT() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose files for interpretation");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("EXP - Полевые данные", "*.EXP", "*.exp")
        );
        File file = chooser.showOpenDialog(mainPane.getScene().getWindow());
        EXPFile openedEXP = new EXPFile();
        STTFile openedSTT = new STTFile();
        Path openedFilePath = file.toPath();
        try {
            openedEXP = Sonet.readEXP(file);
//            openedSTT = Sonet.readSTT();

        } catch (FileNotFoundException noFile) {
            System.out.println("Error: STT file not found.");
        }
//        double[] resApp = ForwardSolver.solve(opened.);
    }


}
