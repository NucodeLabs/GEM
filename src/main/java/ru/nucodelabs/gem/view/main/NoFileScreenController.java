package ru.nucodelabs.gem.view.main;

import javafx.beans.property.BooleanProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.nucodelabs.gem.view.AbstractController;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class NoFileScreenController extends AbstractController {

    @Inject
    private Provider<MainViewController> mainViewControllerProvider;
    @FXML
    public ListView<File> recentFiles;
    @Inject
    private Preferences preferences;

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
        recentFiles.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        recentFiles.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                if (recentFiles.getSelectionModel().getSelectedItems().size() == 1) {
                    mainViewControllerProvider.get().openJsonSection(
                            recentFiles.getSelectionModel().getSelectedItem()
                    );
                }
            }
        });
        initConfig(preferences);
        visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                initConfig(preferences);
            }
        });
    }

    private void initConfig(Preferences preferences) {
        String filesString = preferences.get("RECENT_FILES", "");
        List<String> pathsFromPrefs = List.of(filesString.split(File.pathSeparator));
        List<String> paths = new ArrayList<>(pathsFromPrefs);

        paths = paths.stream()
                .filter(s -> new File(s).exists())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());

        preferences.put("RECENT_FILES", String.join(File.pathSeparator, paths));

        List<File> files = paths.stream()
                .map(File::new)
                .toList();

        recentFiles.getItems().setAll(files);
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
