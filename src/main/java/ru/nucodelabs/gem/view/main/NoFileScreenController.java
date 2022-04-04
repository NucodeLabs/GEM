package ru.nucodelabs.gem.view.main;

import javafx.beans.property.BooleanProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.nucodelabs.gem.view.AbstractController;

import javax.inject.Inject;
import javax.inject.Provider;
import java.net.URL;
import java.util.ResourceBundle;

public class NoFileScreenController extends AbstractController {

    @Inject
    private Provider<MainViewController> mainViewControllerProvider;

    @FXML
    private VBox root;

    @FXML
    private void importEXP() {
        mainViewControllerProvider.get().importEXP();
    }

    @FXML
    private void openSection(Event event) {
        mainViewControllerProvider.get().openSection(event);
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
