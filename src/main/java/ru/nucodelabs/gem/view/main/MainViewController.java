package ru.nucodelabs.gem.view.main;

import com.google.inject.name.Named;
import jakarta.validation.Validator;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.nucodelabs.algorithms.inverse_solver.InverseSolver;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.gem.app.io.StorageManager;
import ru.nucodelabs.gem.utils.FXUtils;
import ru.nucodelabs.gem.utils.OSDetect;
import ru.nucodelabs.gem.view.AbstractController;
import ru.nucodelabs.gem.view.AlertsFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;

public class MainViewController extends AbstractController {

    private final StringProperty vesTitle = new SimpleStringProperty("");
    private final StringProperty vesNumber = new SimpleStringProperty();
    private final BooleanProperty noFileOpened = new SimpleBooleanProperty(true);

    private final ObjectProperty<Picket> picket;
    private final IntegerProperty picketIndex;
    private final ObservableList<Picket> picketObservableList;
    private final StorageManager storageManager;

    @FXML
    private Stage root;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menuView;
    @FXML
    private NoFileScreenController noFileScreenController;
    private ResourceBundle uiProperties;
    @Inject
    @Named("MainView")
    private Provider<Stage> mainViewProvider;
    @Inject
    @Named("EXP")
    private FileChooser expFileChooser;
    @Inject
    @Named("JSON")
    private FileChooser jsonFileChooser;
    @Inject
    @Named("MOD")
    private FileChooser modFileChooser;
    @Inject
    @Named("Save")
    private Provider<Dialog<ButtonType>> saveDialogProvider;
    @Inject
    private AlertsFactory alertsFactory;
    @Inject
    private Validator validator;

    @Inject
    public MainViewController(
            ObjectProperty<Picket> picket,
            IntegerProperty picketIndex,
            ObservableList<Picket> picketObservableList,
            StorageManager storageManager) {
        this.picket = picket;
        this.picketIndex = picketIndex;
        this.picketObservableList = picketObservableList;
        this.storageManager = storageManager;

        vesNumber.bind(new StringBinding() {
            {
                super.bind(picketIndex, picketObservableList);
            }

            @Override
            protected String computeValue() {
                return (picketIndex.get() + 1) + "/" + picketObservableList.size();
            }
        });

        vesTitle.bind(new StringBinding() {
            {
                super.bind(picket);
            }

            @Override
            protected String computeValue() {
                return picket.get() != null ? picket.get().name() : "-";
            }
        });

        noFileOpened.bind(new BooleanBinding() {
            {
                super.bind(picketObservableList);
            }

            @Override
            protected boolean computeValue() {
                return picketObservableList.isEmpty();
            }
        });

        picketObservableList.setAll(storageManager.getSavedState().pickets());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        uiProperties = requireNonNull(resources);
        noFileScreenController.visibleProperty().bind(noFileOpened);

        if (OSDetect.isMacOS()) {
            CheckMenuItem useSystemMenu = new CheckMenuItem(uiProperties.getString("useSystemMenu"));
            menuView.getItems().add(0, useSystemMenu);
            useSystemMenu.selectedProperty().bindBidirectional(menuBar.useSystemMenuBarProperty());
            FXUtils.addCloseShortcutMacOS(getStage().getScene().getRoot());
        }

        getStage().setOnCloseRequest(this::askToSave);
    }

    @Override
    protected Stage getStage() {
        return root;
    }

    @FXML
    public void closeFile(Event event) {
        if (askToSave(event).isConsumed()) {
            return;
        }
        picketObservableList.clear();
        storageManager.clearSavedState();
    }

    @FXML
    public void importEXP() {
        List<File> files = expFileChooser.showOpenMultipleDialog(getStage());
        if (files != null) {
            if (files.get(files.size() - 1).getParentFile().isDirectory()) {
                expFileChooser.setInitialDirectory(files.get(files.size() - 1).getParentFile());
            }
            for (var file : files) {
                addEXP(file);
            }
        }
    }

    @FXML
    public void openSection(Event event) {
        if (askToSave(event).isConsumed()) {
            return;
        }
        File file = jsonFileChooser.showOpenDialog(getStage());
        if (file != null) {
            if (file.getParentFile().isDirectory()) {
                jsonFileChooser.setInitialDirectory(file.getParentFile());
            }

            try {
                Section loadedSection = storageManager.loadSectionFromJsonFile(file);
                var violations =
                        validator.validate(loadedSection);

                if (!violations.isEmpty()) {
                    alertsFactory.violationsAlert(violations, getStage()).show();
                    return;
                }

                picketObservableList.setAll(loadedSection.pickets());
                picketIndex.set(0);
            } catch (Exception e) {
                alertsFactory.incorrectFileAlert(e, getStage()).show();
            }
        }
    }

    private Event askToSave(Event event) {
        if (!storageManager.compareWithSavedState(new Section(picketObservableList))) {
            Dialog<ButtonType> saveDialog = saveDialogProvider.get();
            saveDialog.initOwner(getStage());
            Optional<ButtonType> answer = saveDialog.showAndWait();
            if (answer.isPresent()) {
                if (answer.get() == ButtonType.YES) {
                    saveSection();
                } else if (answer.get() == ButtonType.CANCEL) {
                    event.consume();
                }
            }
        }
        return event;
    }

    @FXML
    public void saveSection() {
        File file = jsonFileChooser.showSaveDialog(getStage());
        if (file != null) {
            if (file.getParentFile().isDirectory()) {
                jsonFileChooser.setInitialDirectory(file.getParentFile());
            }
            try {
                storageManager.saveSectionToJsonFile(file, new Section(picketObservableList));
            } catch (Exception e) {
                alertsFactory.incorrectFileAlert(e, getStage()).show();
            }
        }
    }

    /**
     * Opens new window
     */
    @FXML
    public void newWindow() {
        mainViewProvider.get().show();
    }

    /**
     * Asks which file to import and then import it
     */
    @FXML
    public void importMOD() {
        File file = modFileChooser.showOpenDialog(getStage());

        if (file != null) {
            if (file.getParentFile().isDirectory()) {
                modFileChooser.setInitialDirectory(file.getParentFile());
            }
            try {
                Picket newPicket = storageManager.loadModelDataFromMODFile(file, picket.get());

                var violations = validator.validate(newPicket);

                if (!violations.isEmpty()) {
                    alertsFactory.violationsAlert(violations, getStage());
                    return;
                }

                picket.set(newPicket);
            } catch (Exception e) {
                alertsFactory.incorrectFileAlert(e, getStage()).show();
            }
        }
    }

    @FXML
    public void importJsonPicket() {
        File file = jsonFileChooser.showOpenDialog(getStage());

        if (file != null) {
            if (file.getParentFile().isDirectory()) {
                jsonFileChooser.setInitialDirectory(file.getParentFile());
            }

            try {
                Picket loadedPicket = storageManager.loadPicketFromJsonFile(file);
                picketObservableList.add(loadedPicket);
                picketIndex.set(picketObservableList.size() - 1);
            } catch (Exception e) {
                alertsFactory.incorrectFileAlert(e, getStage()).show();
            }
        }
    }

    @FXML
    public void exportJsonPicket() {
        File file = jsonFileChooser.showSaveDialog(getStage());

        if (file != null) {
            if (file.getParentFile().isDirectory()) {
                jsonFileChooser.setInitialDirectory(file.getParentFile());
            }

            try {
                storageManager.savePicketToJsonFile(file, picket.get());
            } catch (Exception e) {
                alertsFactory.simpleExceptionAlert(e, getStage()).show();
            }
        }
    }

    @FXML
    public void switchToNextPicket() {
        if (picketIndex.get() + 1 <= picketObservableList.size() - 1
                && !picketObservableList.isEmpty()) {
            picketIndex.set(picketIndex.get() + 1);
        }
    }

    @FXML
    public void switchToPrevPicket() {
        if (picketIndex.get() >= 1
                && !picketObservableList.isEmpty()) {
            picketIndex.set(picketIndex.get() - 1);
        }
    }

    @FXML
    public void inverseSolve() {
        InverseSolver inverseSolver = new InverseSolver(picket.get());

        try {
            Picket newPicket = new Picket(
                    picket.get().name(),
                    picket.get().experimentalData(),
                    inverseSolver.getOptimizedModelData()
            );
            picket.set(newPicket);
        } catch (Exception e) {
            alertsFactory.unsafeDataAlert(picket.get().name(), getStage()).show();
        }
    }

    private void addEXP(File file) {
        try {
            Picket picketFromEXPFile = storageManager.loadPicketFromEXPFile(file);
            var violations = validator.validate(picket);
            if (!violations.isEmpty()) {
                alertsFactory.violationsAlert(violations, getStage()).show();
                return;
            }
            picketObservableList.add(picketFromEXPFile);
            picketIndex.set(picketObservableList.size() - 1);
            compatibilityModeAlert();
        } catch (Exception e) {
            alertsFactory.incorrectFileAlert(e, getStage()).show();
        }
    }

    /**
     * Warns about compatibility mode if data is unsafe
     */
    private void compatibilityModeAlert() {
        if (picket.get() != null && picket.get().experimentalData().isUnsafe()) {
            alertsFactory.unsafeDataAlert(picket.get().name(), getStage()).show();
        }
    }

    public String getVesTitle() {
        return vesTitle.get();
    }

    public StringProperty vesTitleProperty() {
        return vesTitle;
    }

    public boolean getNoFileOpened() {
        return noFileOpened.get();
    }

    public BooleanProperty noFileOpenedProperty() {
        return noFileOpened;
    }

    public String getVesNumber() {
        return vesNumber.get();
    }

    public StringProperty vesNumberProperty() {
        return vesNumber;
    }
}
