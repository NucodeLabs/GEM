package ru.nucodelabs.gem.view.tables;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.ModelTableLine;
import ru.nucodelabs.gem.core.events.ModelDraggedEvent;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.view.AbstractSectionController;
import ru.nucodelabs.gem.view.VESTablesConverters;

import java.net.URL;
import java.util.ResourceBundle;

public class ModelTableController extends AbstractSectionController {

    @FXML
    private TableView<ModelTableLine> table;

    public ModelTableController(EventBus eventBus, Section section) {
        super(eventBus, section);
    }

    @Subscribe
    private void handleModelDraggedEvent(ModelDraggedEvent modelDraggedEvent) {
        update();
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
        ObservableList<ModelTableLine> modelTableLines = FXCollections.emptyObservableList();

        if (section.getModelData(currentPicket) != null) {
            modelTableLines = VESTablesConverters.toModelTableData(
                    section.getModelData(currentPicket)
            );
        }

        table.itemsProperty().setValue(modelTableLines);
    }
}
