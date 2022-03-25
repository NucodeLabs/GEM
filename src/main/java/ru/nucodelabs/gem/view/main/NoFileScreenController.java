package ru.nucodelabs.gem.view.main;

import com.google.inject.name.Named;
import javafx.beans.property.BooleanProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.nucodelabs.gem.view.Controller;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class NoFileScreenController extends Controller {

    @Inject
    @Named("ImportEXP")
    private EventHandler<Event> importEXP;
    @Inject
    @Named("OpenJSON")
    private EventHandler<Event> openSection;

    @FXML
    private VBox root;

    @FXML
    private void importEXP(Event event) {
        importEXP.handle(event);
    }

    @FXML
    private void openSection(Event event) {
        openSection.handle(event);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

    public BooleanProperty visibleProperty() {
        return root.visibleProperty();
    }
}
