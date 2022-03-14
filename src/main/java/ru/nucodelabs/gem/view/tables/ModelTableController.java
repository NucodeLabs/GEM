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
import ru.nucodelabs.gem.core.events.PicketSwitchEvent;
import ru.nucodelabs.gem.core.events.SectionChangeEvent;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.VESTablesConverters;

import java.net.URL;
import java.util.ResourceBundle;

public class ModelTableController extends Controller {

    private int currentPicket;
    private Section section;
    private EventBus eventBus;

    @FXML
    private TableView<ModelTableLine> table;

    public void setSection(Section section) {
        this.section = section;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    @Subscribe
    private void handlePicketSwitchEvent(PicketSwitchEvent event) {
        this.currentPicket = event.newPicketNumber();
        update();
    }

    @Subscribe
    private void handleModelDraggedEvent(ModelDraggedEvent modelDraggedEvent) {
        update();
    }

    @Subscribe
    private void handleSectionChangeEvent(SectionChangeEvent event) {
        update();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    protected Stage getStage() {
        return (Stage) table.getScene().getWindow();
    }

    private void update() {
        ObservableList<ModelTableLine> modelTableLines = FXCollections.emptyObservableList();

        if (section.getModelData(currentPicket) != null) {
            modelTableLines = VESTablesConverters.toModelTableData(
                    section.getModelData(currentPicket)
            );
        }

        table.itemsProperty().setValue(modelTableLines);
    }
}
