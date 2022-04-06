package ru.nucodelabs.gem.view.main;

import javafx.beans.property.BooleanProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.nucodelabs.gem.view.AbstractController;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NoFileScreenController extends AbstractController {

    @Inject
    private Provider<MainViewController> mainViewControllerProvider;
    @Inject
    private Logger logger;

    @FXML
    private VBox root;

    @FXML
    private void importEXP() {
        mainViewControllerProvider.get().importEXP();
    }

    @FXML
    private void openSection(Event event) {
        mainViewControllerProvider.get().openJsonSection(event);
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

    @FXML
    private void dragOverHandle(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            List<File> files = dragEvent.getDragboard().getFiles();
            for (var file : files) {
                if (file.getName().endsWith(".EXP") || file.getName().endsWith(".exp")
                        || file.getName().endsWith(".json") || file.getName().endsWith(".JSON")) {
                    dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
            }
        }
        dragEvent.consume();
    }

    @FXML
    private void dragDropHandle(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            logger.log(Level.INFO, "Drag Drop Event");
            List<File> files = dragEvent.getDragboard().getFiles();
            dragEvent.setDropCompleted(true);
            dragEvent.consume();
            for (var file : files) {
                if (file.getName().endsWith(".EXP") || file.getName().endsWith(".exp")) {
                    mainViewControllerProvider.get().addEXP(file);
                } else if (file.getName().endsWith(".json") || file.getName().endsWith(".JSON")) {
                    mainViewControllerProvider.get().openJsonSection(file);
                }
            }
        }
    }
}
