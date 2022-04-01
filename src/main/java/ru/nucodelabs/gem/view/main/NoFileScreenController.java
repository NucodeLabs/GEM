package ru.nucodelabs.gem.view.main;

import javafx.beans.property.BooleanProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.nucodelabs.gem.app.AppManager;
import ru.nucodelabs.gem.view.AbstractController;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class NoFileScreenController extends AbstractController {

    private final AppManager appManager;

    @Inject
    public NoFileScreenController(AppManager appManager) {
        this.appManager = appManager;
    }

    @FXML
    private VBox root;

    @FXML
    private void importEXP(Event event) {
        appManager.importEXP();
    }

    @FXML
    private void openSection(Event event) {
        appManager.openSection(event);
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
