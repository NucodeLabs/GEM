package ru.nucodelabs.gem.view.tables;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.ExperimentalTableLine;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.VESTablesConverters;

import java.net.URL;
import java.util.ResourceBundle;

public class ExperimentalTableController extends Controller implements Initializable {

    private Section section;

    @FXML
    private TableView<ExperimentalTableLine> table;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setSection(Section section) {
        this.section = section;
    }

    @Override
    protected Stage getStage() {
        return (Stage) table.getScene().getWindow();
    }

    public void updateExpTable(int pickerNumber) {
        table.itemsProperty().setValue(
                VESTablesConverters.toExperimentalTableData(
                        section.getExperimentalData(pickerNumber)
                )
        );
    }
}
