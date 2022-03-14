package ru.nucodelabs.gem.view.tables;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.ExperimentalTableLine;
import ru.nucodelabs.gem.core.events.PicketSwitchEvent;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.VESTablesConverters;

import java.net.URL;
import java.util.ResourceBundle;

public class ExperimentalTableController extends Controller {

    private int currentPicket;
    private Section section;
    private EventBus eventBus;

    @FXML
    private TableView<ExperimentalTableLine> table;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    @Subscribe
    private void handlePicketSwitchEvent(PicketSwitchEvent event) {
        currentPicket = event.newPicketNumber();
        update();
    }

    @Override
    protected Stage getStage() {
        return (Stage) table.getScene().getWindow();
    }

    private void update() {
        table.itemsProperty().setValue(
                VESTablesConverters.toExperimentalTableData(
                        section.getExperimentalData(currentPicket)
                )
        );
    }
}
