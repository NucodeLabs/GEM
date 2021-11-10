package ru.nucodelabs.gem;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
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
    @FXML
    public LineChart<Double, Double> vesCurve;
    public LineChart<Double, Double> inaccuracyCurve;

    @FXML
    public TableView<Double> vesTable;

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
            e.printStackTrace();
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
            e.printStackTrace();
            return;
        }
        ExperimentalCurve.makeCurve(vesCurve, openedSTT, openedEXP);
        InaccuracyCurve.makeCurve(inaccuracyCurve, openedSTT, openedEXP);
    }

}
