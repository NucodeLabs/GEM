package ru.nucodelabs.gem.view.main;

import com.google.inject.name.Named;
import jakarta.validation.Validator;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.nucodelabs.algorithms.inverse_solver.InverseSolver;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.gem.app.io.StorageManager;
import ru.nucodelabs.gem.utils.FXUtils;
import ru.nucodelabs.gem.utils.OSDetect;
import ru.nucodelabs.gem.view.AlertsFactory;
import ru.nucodelabs.gem.view.Controller;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;

public class MainViewController extends Controller {

    private final StringProperty vesTitle = new SimpleStringProperty("");
    private final StringProperty vesNumber = new SimpleStringProperty("0/0");
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
        picketObservableList.addListener((ListChangeListener<? super Picket>) c -> {
            if (c.next()) {
                // если список опустошили
                if (c.getList().isEmpty()) {
                    update();
                    picket.set(null);
                    return;
                }
                // если был удален последний пикет в то время когда он отображался
                if (c.wasRemoved()
                        && c.getFrom() == c.getTo()
                        && c.getTo() == picketIndex.get()
                        && picketIndex.get() >= c.getList().size()) {
                    picketIndex.set(max(0, c.getList().size() - 1));
                }
                // если после изменения списка индекс не поменялся, но отображается не соответсвующий списку пикет
                if (picket.get() == null
                        || !picket.get().equals(c.getList().get(picketIndex.get()))) {
                    picket.set(c.getList().get(picketIndex.get()));
                }
            }
        });
        // если индекс поменялся, поменять пикет на тот, что в списке по индексу
        picketIndex.addListener((observable, oldValue, newValue) -> {
            picket.set(picketObservableList.get(newValue.intValue()));
            update();
        });
        // если пикет изменился, но не переключился, а поменял значения, то заносим его в список
        picket.addListener((observable, oldValue, newValue) -> {
            if (picketObservableList.stream().noneMatch(p -> p.equals(newValue))
                    && !picketObservableList.isEmpty()) {
                picketObservableList.set(picketIndex.get(), newValue);
            }
            update();
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
     * Adds files names to vesText
     */
    private void updateVESText() {
        vesTitle.set(picketObservableList.get(picketIndex.get()).name());
    }

    private void updateVESNumber() {
        vesNumber.set(picketIndex.get() + 1 + "/" + picketObservableList.size());
    }

    /**
     * Warns about compatibility mode if data is unsafe
     */
    private void compatibilityModeAlert() {
        ExperimentalData experimentalData = picket.get().experimentalData();
        if (experimentalData != null && experimentalData.isUnsafe()) {
            alertsFactory.unsafeDataAlert(picket.get().name(), getStage()).show();
        }
    }

    protected void update() {
        noFileOpened.set(picketObservableList.isEmpty());
        if (!picketObservableList.isEmpty()) {
            updateVESText();
            updateVESNumber();
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
