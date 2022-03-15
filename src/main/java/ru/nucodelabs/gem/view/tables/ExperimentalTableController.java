package ru.nucodelabs.gem.view.tables;

import com.google.common.eventbus.EventBus;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.ExperimentalTableLine;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.view.AbstractSectionController;
import ru.nucodelabs.gem.view.VESTablesConverters;

import java.net.URL;
import java.util.ResourceBundle;

public class ExperimentalTableController extends AbstractSectionController {

    @FXML
    private TableView<ExperimentalTableLine> table;

    public ExperimentalTableController(EventBus eventBus, Section section) {
        super(eventBus, section);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    protected Stage getStage() {
        return (Stage) table.getScene().getWindow();
    }

    @Override
    protected void update() {
        table.itemsProperty().setValue(
                VESTablesConverters.toExperimentalTableData(
                        section.getExperimentalData(currentPicket)
                )
        );
    }
}
