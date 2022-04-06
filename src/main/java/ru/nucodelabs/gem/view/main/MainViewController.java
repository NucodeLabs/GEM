package ru.nucodelabs.gem.view.main;

import com.google.inject.name.Named;
import com.sun.glass.ui.Application;
import jakarta.validation.Validator;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.gem.app.HistoryManager;
import ru.nucodelabs.gem.app.io.StorageManager;
import ru.nucodelabs.gem.app.model.AbstractSectionObserver;
import ru.nucodelabs.gem.app.model.SectionManager;
import ru.nucodelabs.gem.utils.OSDetect;
import ru.nucodelabs.gem.view.AbstractController;
import ru.nucodelabs.gem.view.AlertsFactory;
import ru.nucodelabs.gem.view.charts.VESCurvesController;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainViewController extends AbstractController {

    private final StringProperty vesTitle = new SimpleStringProperty("");
    private final StringProperty vesNumber = new SimpleStringProperty();
    private final BooleanProperty noFileOpened = new SimpleBooleanProperty(true);

    private final StringProperty windowTitle = new SimpleStringProperty("GEM");
    private final StringProperty dirtyAsterisk = new SimpleStringProperty("");

    @FXML
    private CheckMenuItem menuViewVESCurvesLegend;
    @FXML
    private Stage root;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menuView;
    @FXML
    private NoFileScreenController noFileScreenController;
    @FXML
    private VESCurvesController vesCurvesController;

    @Inject
    private ObservableObjectValue<Picket> picket;
    @Inject
    private IntegerProperty picketIndex;
    @Inject
    private ObservableList<Picket> picketObservableList;
    @Inject
    private SectionManager sectionManager;
    @Inject
    @Named("MainView")
    private Provider<Stage> mainViewProvider;
    @Inject
    private HistoryManager historyManager;
    @Inject
    private AlertsFactory alertsFactory;
    @Inject
    @Named("EXP")
    private FileChooser expFileChooser;
    @Inject
    @Named("MOD")
    private FileChooser modFileChooser;
    @Inject
    @Named("JSON")
    private FileChooser jsonFileChooser;
    @Inject
    private StorageManager storageManager;
    @Inject
    @Named("Save")
    private Provider<Dialog<ButtonType>> saveDialogProvider;
    @Inject
    private Validator validator;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getStage().setOnCloseRequest(this::askToSave);
        getStage().getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN),
                this::redo);

        noFileScreenController.visibleProperty().bind(noFileOpened);
        vesCurvesController.legendVisibleProperty().bind(menuViewVESCurvesLegend.selectedProperty());

        if (OSDetect.isMacOS()) {
            CheckMenuItem useSystemMenu = new CheckMenuItem(resources.getString("useSystemMenu"));
            menuView.getItems().add(0, useSystemMenu);
            useSystemMenu.selectedProperty().bindBidirectional(menuBar.useSystemMenuBarProperty());

            Application macSpecificApp = Application.GetApplication();
            macSpecificApp.setEventHandler(new Application.EventHandler() {
                @Override
                public void handleQuitAction(Application app, long time) {
                    getStage().fireEvent(new WindowEvent(getStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
                }
            });
        }

        bind();
    }

    private void bind() {
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

        sectionManager.subscribe(new AbstractSectionObserver() {
            @Override
            public void onNext(Section item) {
                if (!storageManager.compareWithSavedState(item)) {
                    dirtyAsterisk.set("*");
                } else {
                    dirtyAsterisk.set("");
                }
            }
        });

        getStage().titleProperty().bind(Bindings.concat(dirtyAsterisk, windowTitle));
    }

    @Override
    protected Stage getStage() {
        return root;
    }

    @FXML
    private void closeFile(Event event) {
        if (askToSave(event).isConsumed()) {
            return;
        }
        storageManager.clearSavedState();
        sectionManager.setSection(new Section(Collections.emptyList()));
        historyManager.clear();
        resetWindowTitle();
    }

    private Event askToSave(Event event) {
        if (!storageManager.compareWithSavedState(sectionManager.getSnapshot())) {
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

    public void addEXP(File file) {
        try {
            Picket picketFromEXPFile = storageManager.loadNameAndExperimentalDataFromEXPFile(file);
            var violations = validator.validate(picketFromEXPFile);
            if (!violations.isEmpty()) {
                alertsFactory.violationsAlert(violations, getStage()).show();
                return;
            }
            historyManager.performThenSnapshot(() -> sectionManager.add(picketFromEXPFile));
            picketIndex.set(sectionManager.size() - 1);
            compatibilityModeAlert();
        } catch (Exception e) {
            alertsFactory.incorrectFileAlert(e, getStage()).show();
        }
    }


    @FXML
    public void openJsonSection(Event event) {
        if (askToSave(event).isConsumed()) {
            return;
        }
        File file = jsonFileChooser.showOpenDialog(getStage());
        if (file != null) {
            if (file.getParentFile().isDirectory()) {
                jsonFileChooser.setInitialDirectory(file.getParentFile());
            }
            openJsonSection(file);
        }
    }

    public void openJsonSection(File file) {
        try {
            Section loadedSection = storageManager.loadFromJson(file, Section.class);
            var violations =
                    validator.validate(loadedSection);

            if (!violations.isEmpty()) {
                alertsFactory.violationsAlert(violations, getStage()).show();
                return;
            }

            sectionManager.setSection(loadedSection);
            picketIndex.set(0);
            historyManager.clear();
            historyManager.snapshot();
            setWindowFileTitle(file);
        } catch (Exception e) {
            alertsFactory.incorrectFileAlert(e, getStage()).show();
        }
    }

    @FXML
    private void saveSection() {
        File file;
        if (storageManager.getSavedStateFile() == null) {
            file = jsonFileChooser.showSaveDialog(getStage());
        } else {
            file = storageManager.getSavedStateFile();
        }
        saveSection(file);
    }

    @FXML
    private void saveSectionAs() {
        saveSection(jsonFileChooser.showSaveDialog(getStage()));
    }

    /**
     * Opens new window
     */
    @FXML
    private void newWindow() {
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
            importMOD(file);
        }
    }

    public void importMOD(File file) {
        try {
            Picket newPicket = storageManager.loadModelDataFromMODFile(file, sectionManager.get(picketIndex.get()));

            var violations = validator.validate(newPicket);

            if (!violations.isEmpty()) {
                alertsFactory.violationsAlert(violations, getStage());
                return;
            }

            historyManager.performThenSnapshot(() -> sectionManager.updatePicket(picketIndex.get(), newPicket));
        } catch (Exception e) {
            alertsFactory.incorrectFileAlert(e, getStage()).show();
        }
    }

    @FXML
    private void importJsonPicket() {
        File file = jsonFileChooser.showOpenDialog(getStage());

        if (file != null) {
            if (file.getParentFile().isDirectory()) {
                jsonFileChooser.setInitialDirectory(file.getParentFile());
            }

            importJsonPicket(file);
        }
    }

    public void importJsonPicket(File file) {
        try {
            Picket loadedPicket = storageManager.loadFromJson(file, Picket.class);
            historyManager.performThenSnapshot(() -> sectionManager.add(loadedPicket));
            picketIndex.set(sectionManager.size() - 1);
        } catch (Exception e) {
            alertsFactory.incorrectFileAlert(e, getStage()).show();
        }
    }

    @FXML
    private void exportJsonPicket() {
        File file = jsonFileChooser.showSaveDialog(getStage());

        if (file != null) {
            if (file.getParentFile().isDirectory()) {
                jsonFileChooser.setInitialDirectory(file.getParentFile());
            }

            try {
                storageManager.saveToJson(file, sectionManager.get(picketIndex.get()));
            } catch (Exception e) {
                alertsFactory.simpleExceptionAlert(e, getStage()).show();
            }
        }
    }

    @FXML
    private void switchToNextPicket() {
        if (picketIndex.get() + 1 <= picketObservableList.size() - 1
                && !picketObservableList.isEmpty()) {
            picketIndex.set(picketIndex.get() + 1);
        }
    }

    @FXML
    private void switchToPrevPicket() {
        if (picketIndex.get() >= 1
                && !picketObservableList.isEmpty()) {
            picketIndex.set(picketIndex.get() - 1);
        }
    }

    @FXML
    private void inverseSolve() {
        try {
            historyManager.performThenSnapshot(() -> sectionManager.inverseSolve(picketIndex.get()));
        } catch (Exception e) {
            alertsFactory.simpleExceptionAlert(e, getStage()).show();
        }
    }

    private void compatibilityModeAlert() {
        if (sectionManager.get(picketIndex.get()) != null
                && sectionManager.get(picketIndex.get()).experimentalData().isUnsafe()) {
            alertsFactory.unsafeDataAlert(sectionManager.get(picketIndex.get()).name(), getStage()).show();
        }
    }

    private void saveSection(File file) {
        if (file != null) {
            if (file.getParentFile().isDirectory()) {
                jsonFileChooser.setInitialDirectory(file.getParentFile());
            }
            try {
                storageManager.saveToJson(file, sectionManager.getSnapshot());
                setWindowFileTitle(file);
                dirtyAsterisk.set("");
            } catch (Exception e) {
                alertsFactory.incorrectFileAlert(e, getStage()).show();
            }
        }
    }


    @FXML
    private void undo() {
        historyManager.undo();
    }

    @FXML
    private void redo() {
        historyManager.redo();
    }

    private void setWindowFileTitle(File file) {
        windowTitle.set(file.getName());
    }

    private void resetWindowTitle() {
        windowTitle.set("GEM");
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
