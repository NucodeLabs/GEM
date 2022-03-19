package ru.nucodelabs.gem.view.tables;

import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.ExperimentalTableLine;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.convert.VESTablesConverters;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class ExperimentalTableController extends Controller {


    private final ObjectProperty<Picket> picket;

    @FXML
    private TableView<ExperimentalTableLine> table;

    @Inject
    public ExperimentalTableController(ObjectProperty<Picket> picket) {

        this.picket = picket;
        picket.addListener((observable, oldValue, newValue) -> {
            if (oldValue == null) {
                update();
            } else if (oldValue.experimentalData() != null
                    && !oldValue.experimentalData().equals(newValue.experimentalData())) {
                update();
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    protected Stage getStage() {
        return (Stage) table.getScene().getWindow();
    }

    protected void update() {
        table.itemsProperty().setValue(
                VESTablesConverters.toExperimentalTableData(
                        picket.get().experimentalData()
                )
        );
    }
}
