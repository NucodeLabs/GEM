package ru.nucodelabs.gem.view.main;

import com.google.inject.name.Named;
import jakarta.validation.Validator;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableObjectValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.gem.app.io.StorageManager;
import ru.nucodelabs.gem.app.model.AbstractSectionObserver;
import ru.nucodelabs.gem.app.model.SectionManager;
import ru.nucodelabs.gem.app.pref.FXPreferences;
import ru.nucodelabs.gem.app.snapshot.HistoryManager;
import ru.nucodelabs.gem.app.snapshot.Snapshot;
import ru.nucodelabs.gem.utils.FXUtils;
import ru.nucodelabs.gem.utils.OS;
import ru.nucodelabs.gem.view.AbstractController;
import ru.nucodelabs.gem.view.AlertsFactory;
import ru.nucodelabs.gem.view.charts.VesCurvesController;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static ru.nucodelabs.gem.app.pref.AppPreferencesKt.*;
import static ru.nucodelabs.gem.app.pref.UIPreferencesKt.VES_CURVES_LEGEND_VISIBLE;


public class MainViewController extends AbstractController implements FileImporter, FileOpener {

    private final StringProperty vesNumber = new SimpleStringProperty();
    private final BooleanProperty noFileOpened = new SimpleBooleanProperty(true);

    private final StringProperty windowTitle = new SimpleStringProperty("GEM");
    private final StringProperty dirtyAsterisk = new SimpleStringProperty("");

    @FXML
    private TextField picketName;
    @FXML
    private Label xCoordLbl;
    @FXML
    private TextField picketZ;
    @FXML
    private TextField picketOffsetX;
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
    private VesCurvesController vesCurvesController;

    @Inject
    private ObservableObjectValue<Picket> picket;
    @Inject
    private IntegerProperty picketIndex;
    @Inject
    private ObservableObjectValue<Section> section;
    @Inject
    private SectionManager sectionManager;
    @Inject
    @Named("MainView")
    private Provider<Stage> mainViewProvider;
    @Inject
    private HistoryManager<Section> historyManager;
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
    @Inject
    private Preferences preferences;
    @Inject
    private DecimalFormat decimalFormat;
    @Inject
    private FXPreferences fxPreferences;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getStage().setOnCloseRequest(this::askToSave);
        getStage().getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN),
                this::redo);


        if (OS.isMacOS()) {
            CheckMenuItem useSystemMenu = new CheckMenuItem(resources.getString("useSystemMenu"));
            menuView.getItems().add(0, useSystemMenu);
            useSystemMenu.selectedProperty().bindBidirectional(menuBar.useSystemMenuBarProperty());

            final String prefKey = "USE_SYSTEM_MENU";
            final boolean defVal = true;
            useSystemMenu.setSelected(preferences.getBoolean(prefKey, defVal));
            useSystemMenu.selectedProperty().addListener((observable, oldValue, newValue) ->
                    preferences.putBoolean(prefKey, newValue));
            preferences.addPreferenceChangeListener(evt ->
                    Platform.runLater(() -> {
                        if (evt.getKey().equals(prefKey)) {
                            useSystemMenu.setSelected(Boolean.parseBoolean(evt.getNewValue()));
                        }
                    })
            );
        }

        bind();
        initConfig();

        setupValidationOnPicketXZ(picketOffsetX);
        setupValidationOnPicketXZ(picketZ);
    }

    private BooleanProperty setupValidationOnPicketXZ(TextField tf) {
        return FXUtils.TextFieldValidationSetup.of(tf)
                .validateWith(this::validateDoubleInput)
                .applyStyleIfInvalid("-fx-background-color: LightPink")
                .done();
    }

    private boolean validateDoubleInput(String s) {
        try {
            decimalFormat.parse(s);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    private void initConfig() {
        fxPreferences.bind(getStage().widthProperty(), MAIN_WINDOW_W.getKey(), MAIN_WINDOW_W.getDef(), getStage()::setWidth);
        fxPreferences.bind(getStage().heightProperty(), MAIN_WINDOW_H.getKey(), MAIN_WINDOW_H.getDef(), getStage()::setHeight);
        fxPreferences.bind(getStage().xProperty(), MAIN_WINDOW_X.getKey(), MAIN_WINDOW_X.getDef(), getStage()::setX);
        fxPreferences.bind(getStage().yProperty(), MAIN_WINDOW_Y.getKey(), MAIN_WINDOW_Y.getDef(), getStage()::setY);
        fxPreferences.bind(menuViewVESCurvesLegend.selectedProperty(), VES_CURVES_LEGEND_VISIBLE.getKey(), VES_CURVES_LEGEND_VISIBLE.getDef());
    }

    private void bind() {
        noFileScreenController.visibleProperty().bind(noFileOpened);
        vesCurvesController.legendVisibleProperty().bind(menuViewVESCurvesLegend.selectedProperty());

        vesNumber.bind(Bindings.createStringBinding(
                () -> (picketIndex.get() + 1) + "/" + section.get().getPickets().size(),
                picketIndex, section
        ));

        picket.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                picketName.setText(newValue.getName());
            } else {
                picketName.setText("-");
            }
        });

        noFileOpened.bind(Bindings.createBooleanBinding(
                () -> section.get().getPickets().isEmpty(),
                section
        ));

        sectionManager.getSectionObservable().subscribe(new AbstractSectionObserver() {
            @Override
            public void onNext(Section item) {
                if (!storageManager.getSavedSnapshot().equals(sectionManager.snapshot())) {
                    dirtyAsterisk.set("*");
                } else {
                    dirtyAsterisk.set("");
                }
            }
        });

        getStage().titleProperty().bind(Bindings.concat(dirtyAsterisk, windowTitle));

        picket.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                picketOffsetX.setText(decimalFormat.format(newValue.getOffsetX()));
                picketZ.setText(decimalFormat.format(newValue.getZ()));
                xCoordLbl.setText(decimalFormat.format(section.get().xOfPicket(picket.get())));
            }
        });
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
        storageManager.resetSavedSnapshot();
        sectionManager.restoreFromSnapshot(Snapshot.of(Section.DEFAULT));
        historyManager.clear();
        resetWindowTitle();
    }

    private Event askToSave(Event event) {
        if (!storageManager.getSavedSnapshot().equals(sectionManager.snapshot())) {
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

    @Override
    @FXML
    public void importEXP() {
        List<File> files = expFileChooser.showOpenMultipleDialog(getStage());
        if (files != null) {
            if (files.get(files.size() - 1).getParentFile().isDirectory()) {
                expFileChooser.setInitialDirectory(files.get(files.size() - 1).getParentFile());
                preferences.put(EXP_FILES_DIR.getKey(), files.get(files.size() - 1).getParentFile().getAbsolutePath());
            }
            for (var file : files) {
                importEXP(file);
            }
        }
    }

    @Override
    public void importEXP(@NotNull File file) {
        try {
            Picket picketFromEXPFile = storageManager.loadNameAndExperimentalDataFromEXPFile(file, Picket.createDefaultWithNewId());
            var violations = validator.validate(picketFromEXPFile);
            if (!violations.isEmpty()) {
                alertsFactory.violationsAlert(violations, getStage()).show();
                return;
            }
            historyManager.snapshotAfter(() -> sectionManager.add(picketFromEXPFile));
            picketIndex.set(sectionManager.size() - 1);
        } catch (Exception e) {
            alertsFactory.incorrectFileAlert(e, getStage()).show();
        }
    }


    @Override
    @FXML
    public void openJsonSection(@NotNull Event event) {
        if (askToSave(event).isConsumed()) {
            return;
        }
        File file = jsonFileChooser.showOpenDialog(getStage());
        if (file != null) {
            if (file.getParentFile().isDirectory()) {
                jsonFileChooser.setInitialDirectory(file.getParentFile());
                preferences.put(JSON_FILES_DIR.getKey(), file.getParentFile().getAbsolutePath());
            }
            openJsonSection(file);
        }
    }

    @Override
    public void openJsonSection(@NotNull File file) {
        try {
            Section loadedSection = storageManager.loadFromJson(file, Section.class);
            var violations =
                    validator.validate(loadedSection);

            if (!violations.isEmpty()) {
                alertsFactory.violationsAlert(violations, getStage()).show();
                storageManager.resetSavedSnapshot();
                return;
            }

            sectionManager.restoreFromSnapshot(Snapshot.of(loadedSection));
            picketIndex.set(0);
            historyManager.clear();
            historyManager.snapshot();
            setWindowFileTitle(file);
            preferences.put(RECENT_FILES.getKey(), file.getAbsolutePath()
                    + File.pathSeparator
                    + preferences.get(RECENT_FILES.getKey(), RECENT_FILES.getDef()));
        } catch (Exception e) {
            alertsFactory.incorrectFileAlert(e, getStage()).show();
        }

    }

    @FXML
    private void saveSection() {
        if (!storageManager.getSavedSnapshot().equals(sectionManager.snapshot())) {
            saveSection(storageManager.getSavedSnapshotFile() != null ?
                    storageManager.getSavedSnapshotFile() :
                    jsonFileChooser.showSaveDialog(getStage()));
        }
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
    @Override
    @FXML
    public void importMOD() {
        File file = modFileChooser.showOpenDialog(getStage());

        if (file != null) {
            if (file.getParentFile().isDirectory()) {
                modFileChooser.setInitialDirectory(file.getParentFile());
                preferences.put(MOD_FILES_DIR.getKey(), file.getParentFile().getAbsolutePath());
            }
            importMOD(file);
        }
    }

    @Override
    public void importMOD(@NotNull File file) {
        try {
            Picket newPicket = storageManager.loadModelDataFromMODFile(file, picket.get());

            var violations = validator.validate(newPicket);

            if (!violations.isEmpty()) {
                alertsFactory.violationsAlert(violations, getStage());
                return;
            }

            historyManager.snapshotAfter(() -> sectionManager.update(newPicket));
        } catch (Exception e) {
            alertsFactory.incorrectFileAlert(e, getStage()).show();
        }
    }

    @Override
    public void importJsonPicket() {
        File file = jsonFileChooser.showOpenDialog(getStage());

        if (file != null) {
            if (file.getParentFile().isDirectory()) {
                jsonFileChooser.setInitialDirectory(file.getParentFile());
                preferences.put(JSON_FILES_DIR.getKey(), file.getParentFile().getAbsolutePath());
            }

            importJsonPicket(file);
        }
    }

    @Override
    public void importJsonPicket(@NotNull File file) {
        try {
            Picket loadedPicket = storageManager.loadFromJson(file, Picket.class);
            historyManager.snapshotAfter(() -> sectionManager.add(loadedPicket));
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
                preferences.put(JSON_FILES_DIR.getKey(), file.getParentFile().getAbsolutePath());
            }

            try {
                storageManager.saveToJson(file, sectionManager.getById(picket.get().getId()).orElse(Picket.createDefaultWithNewId()));
            } catch (Exception e) {
                alertsFactory.simpleExceptionAlert(e, getStage()).show();
            }
        }
    }

    @FXML
    private void switchToNextPicket() {
        if (picketIndex.get() + 1 <= section.get().getPickets().size() - 1
                && !section.get().getPickets().isEmpty()) {
            picketIndex.set(picketIndex.get() + 1);
        }
    }

    @FXML
    private void switchToPrevPicket() {
        if (picketIndex.get() >= 1
                && !section.get().getPickets().isEmpty()) {
            picketIndex.set(picketIndex.get() - 1);
        }
    }

    @FXML
    private void inverseSolve() {
        try {
            historyManager.snapshotAfter(() -> sectionManager.inverseSolve(picket.get()));
        } catch (Exception e) {
            alertsFactory.simpleExceptionAlert(e, getStage()).show();
        }
    }

    private void saveSection(File file) {
        if (file != null) {
            if (file.getParentFile().isDirectory()) {
                jsonFileChooser.setInitialDirectory(file.getParentFile());
                preferences.put(JSON_FILES_DIR.getKey(), file.getParentFile().getAbsolutePath());
                preferences.put(RECENT_FILES.getKey(), file.getAbsolutePath()
                        + File.pathSeparator
                        + preferences.get(RECENT_FILES.getKey(), RECENT_FILES.getDef()));
            }
            try {
                storageManager.saveToJson(file, sectionManager.snapshot().getValue());
                setWindowFileTitle(file);
                dirtyAsterisk.set("");
            } catch (Exception e) {
                alertsFactory.incorrectFileAlert(e, getStage()).show();
            }
        }
    }

    @FXML
    private void submitOffsetX() {
        double offsetX;

        try {
            offsetX = decimalFormat.parse(picketOffsetX.getText()).doubleValue();
        } catch (ParseException e) {
            alertsFactory.simpleExceptionAlert(e, getStage()).show();
            picketOffsetX.selectAll();
            return;
        }

        Picket modified = picket.get().withOffsetX(offsetX);
        var violations = validator.validate(modified);
        if (!violations.isEmpty()) {
            alertsFactory.violationsAlert(violations, getStage()).show();
            picketOffsetX.selectAll();
        } else {
            historyManager.snapshotAfter(() ->
                    sectionManager.update(modified));
            FXUtils.unfocus(picketOffsetX);
        }
    }

    @FXML
    private void submitPicketName() {
        historyManager.snapshotAfter(() -> sectionManager.update(picket.get().withName(picketName.getText())));
        FXUtils.unfocus(picketName);
    }

    @FXML
    private void submitZ() {
        double z;

        try {
            z = decimalFormat.parse(picketZ.getText()).doubleValue();
        } catch (ParseException e) {
            alertsFactory.simpleExceptionAlert(e, getStage()).show();
            picketZ.selectAll();
            return;
        }

        Picket modified = picket.get().withZ(z);
        var violations = validator.validate(modified);
        if (!violations.isEmpty()) {
            alertsFactory.violationsAlert(violations, getStage()).show();
            picketZ.selectAll();
        } else {
            historyManager.snapshotAfter(() ->
                    sectionManager.update(modified));
            FXUtils.unfocus(picketZ);
        }
    }

    @Override
    @FXML
    public void addNewPicket() {
        historyManager.snapshotAfter(() -> sectionManager.add(Picket.createDefaultWithNewId()));
        picketIndex.set(sectionManager.size() - 1);
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
