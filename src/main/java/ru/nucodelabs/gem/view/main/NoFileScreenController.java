package ru.nucodelabs.gem.view.main;

import javafx.beans.property.BooleanProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.nucodelabs.gem.app.AppService;
import ru.nucodelabs.gem.view.AbstractController;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class NoFileScreenController extends AbstractController {

    private final AppService appService;

    @Inject
    public NoFileScreenController(AppService appService) {
        this.appService = appService;
    }

    @FXML
    private VBox root;

    @FXML
    private void importEXP(Event event) {
        appService.importEXP();
    }

    @FXML
    private void openSection(Event event) {
        appService.openSection(event);
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
