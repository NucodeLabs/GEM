package ru.nucodelabs.gem.view.tables;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.ModelTableLine;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.VESTablesConverters;

import java.net.URL;
import java.util.ResourceBundle;

public class ModelTableController extends Controller implements Initializable {

    private Section section;

    @FXML
    private TableView<ModelTableLine> table;


    public void setSection(Section section) {
        this.section = section;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    protected Stage getStage() {
        return (Stage) table.getScene().getWindow();
    }

    public void updateModelTable(int picketNumber) {
        ObservableList<ModelTableLine> modelTableLines = FXCollections.emptyObservableList();

        if (section.getModelData(picketNumber) != null) {
            modelTableLines = VESTablesConverters.toModelTableData(
                    section.getModelData(picketNumber)
            );
        }

        table.itemsProperty().setValue(modelTableLines);
    }
}
