package ru.nucodelabs.gem;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.STTFile;
import ru.nucodelabs.files.sonet.Sonet;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;

public class AppController {

    @FXML
    public VBox mainPane;
    public MenuItem menuFileOpenEXP;
    public TitledPane vesPane;
    public LineChart<Double, Double> vesCurve;
    public LineChart<Double, Double> inaccuracyCurve;
    public TableView<Double> vesTable;
    public MenuItem menuFileOpenMOD;
    public TitledPane inaccuracyPane;
    public SplitPane vesSplitPane;

    @FXML
    public void onMenuFileOpenEXP() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose files for interpretation");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("EXP - Полевые данные", "*.EXP", "*.exp")
        );
        File file = chooser.showOpenDialog(mainPane.getScene().getWindow());
//      если закрыть окно выбора файла, ничего не выбрав, то FileChooser вернет null
        if (file == null) {
            return;
        }

        EXPFile openedEXP;
        STTFile openedSTT;
        Path openedFilePath = file.toPath();

        try {
            openedEXP = Sonet.readEXP(file);
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("File not found!");
            alert.setContentText(e.getMessage());
            alert.show();
            return;
        }

        try {
            openedSTT = Sonet.readSTT(new File(
                    openedFilePath.getParent().toString()
                            + File.separator
                            + openedEXP.getSTTFileName()));
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("STT file not found!");
            alert.setContentText(e.getMessage());
            alert.show();
            return;
        }
        try {
            App.primaryStage.setTitle(file.getName() + " - GEM");
            ExperimentalCurve.makeCurve(vesCurve, openedSTT, openedEXP);
            InaccuracyCurve.makeCurve(inaccuracyCurve, openedSTT, openedEXP);
        } catch (IndexOutOfBoundsException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cannot open files");
            alert.setHeaderText("STT and EXP files lines mismatch");
            alert.setContentText(e.getMessage());
            alert.show();
            return;
        }
    }

    @FXML
    public void onMenuFileOpenMOD() {
    }
}
