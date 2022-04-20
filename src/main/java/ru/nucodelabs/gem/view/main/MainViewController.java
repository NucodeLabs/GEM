package ru.nucodelabs.gem.view.main;

import com.google.inject.name.Named;
import jakarta.validation.Validator;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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
import javafx.util.StringConverter;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.data.ves.VesUtils;
import ru.nucodelabs.gem.app.io.StorageManager;
import ru.nucodelabs.gem.app.model.AbstractSectionObserver;
import ru.nucodelabs.gem.app.model.SectionManager;
import ru.nucodelabs.gem.app.snapshot.HistoryManager;
import ru.nucodelabs.gem.app.snapshot.Snapshot;
import ru.nucodelabs.gem.utils.FXUtils;
import ru.nucodelabs.gem.utils.OSDetect;
import ru.nucodelabs.gem.view.AbstractController;
import ru.nucodelabs.gem.view.AlertsFactory;
import ru.nucodelabs.gem.view.charts.VESCurvesController;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class MainViewController extends AbstractController {

    private final StringProperty vesTitle = new SimpleStringProperty("");
    private final StringProperty vesNumber = new SimpleStringProperty();
    private final BooleanProperty noFileOpened = new SimpleBooleanProperty(true);

    private final StringProperty windowTitle = new SimpleStringProperty("GEM");
    private final StringProperty dirtyAsterisk = new SimpleStringProperty("");

    @FXML
    private Label xCoordLbl;
    @FXML
    private Button submitCoodsBtn;
    @FXML
    private TextField picketZ;
    @FXML
    private TextField picketX;
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
    private StringConverter<Double> doubleStringConverter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getStage().setOnCloseRequest(this::askToSave);
        getStage().getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN),
                this::redo);


        if (OSDetect.isMacOS()) {
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
        initConfig(preferences);

        BooleanBinding picketXZValid
                = Bindings.and(setupValidationOnPicketXZ(picketX), setupValidationOnPicketXZ(picketZ));
        submitCoodsBtn.disableProperty().bind(picketXZValid.not());

        FXUtils.addSubmitOnEnter(submitCoodsBtn, picketX, picketZ);
    }

    private BooleanProperty setupValidationOnPicketXZ(TextField picketX) {
        return FXUtils.setupValidation(picketX)
                .validateWith(
                        s -> {
                            try {
                                decimalFormat.parse(s);
                            } catch (ParseException e) {
                                return false;
                            }
                            return true;
                        }
                )
                .applyStyleIfInvalid("-fx-background-color: LightPink")
                .done();
    }

    private void initConfig(Preferences preferences) {
        getStage().setWidth(preferences.getDouble("WINDOW_WIDTH", 1280));
        getStage().setHeight(preferences.getDouble("WINDOW_HEIGHT", 720));
        getStage().setX(preferences.getDouble("WINDOW_X", 100));
        getStage().setY(preferences.getDouble("WINDOW_Y", 100));

        getStage().xProperty().addListener((observable, oldValue, newValue) ->
                preferences.putDouble("WINDOW_X", newValue.doubleValue()));
        getStage().yProperty().addListener((observable, oldValue, newValue) ->
                preferences.putDouble("WINDOW_Y", newValue.doubleValue()));

        getStage().widthProperty().addListener((observable, oldValue, newValue) ->
                preferences.putDouble("WINDOW_WIDTH", newValue.doubleValue()));
        getStage().heightProperty().addListener((observable, oldValue, newValue) ->
                preferences.putDouble("WINDOW_HEIGHT", newValue.doubleValue()));

        menuViewVESCurvesLegend.setSelected(preferences.getBoolean("VES_CURVES_LEGEND_VISIBLE", false));
        menuViewVESCurvesLegend.selectedProperty().addListener((observable, oldValue, newValue) ->
                preferences.putBoolean("VES_CURVES_LEGEND_VISIBLE", newValue));
    }

    private void bind() {
        noFileScreenController.visibleProperty().bind(noFileOpened);
        vesCurvesController.legendVisibleProperty().bind(menuViewVESCurvesLegend.selectedProperty());

        vesNumber.bind(Bindings.createStringBinding(
                () -> (picketIndex.get() + 1) + "/" + picketObservableList.size(),
                picketIndex, picketObservableList
        ));

        vesTitle.bind(Bindings.createStringBinding(
                () -> picket.get() != null ? picket.get().getName() : "-",
                picket
        ));

        noFileOpened.bind(Bindings.createBooleanBinding(
                () -> picketObservableList.isEmpty(),
                picketObservableList
        ));

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

        picket.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                picketX.setText(decimalFormat.format(newValue.getOffsetX()));
                picketZ.setText(decimalFormat.format(newValue.getZ()));
                xCoordLbl.setText(decimalFormat.format(VesUtils.xOfPicket(sectionManager.getSnapshot().get(), picketIndex.get())));
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
        storageManager.clearSavedState();
        sectionManager.restoreFromSnapshot(Snapshot.create(Section.create(Collections.emptyList())));
        historyManager.clear();
        resetWindowTitle();
    }

    private Event askToSave(Event event) {
        if (!storageManager.compareWithSavedState(sectionManager.getSnapshot().get())) {
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
                preferences.put("EXP_FC_INIT_DIR", files.get(files.size() - 1).getParentFile().getAbsolutePath());
            }
            for (var file : files) {
                addEXP(file);
            }
        }
    }

    public void addEXP(File file) {
        try {
            Picket picketFromEXPFile = storageManager.loadNameAndExperimentalDataFromEXPFile(file, Picket.EMPTY);
            var violations = validator.validate(picketFromEXPFile);
            if (!violations.isEmpty()) {
                alertsFactory.violationsAlert(violations, getStage()).show();
                return;
            }
            historyManager.performThenSnapshot(() -> sectionManager.add(picketFromEXPFile));
            picketIndex.set(sectionManager.size() - 1);
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
                preferences.put("JSON_FC_INIT_DIR", file.getParentFile().getAbsolutePath());
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
                storageManager.clearSavedState();
                return;
            }

            sectionManager.restoreFromSnapshot(Snapshot.create(loadedSection));
            picketIndex.set(0);
            historyManager.clear();
            historyManager.snapshot();
            setWindowFileTitle(file);
            preferences.put("RECENT_FILES", file.getAbsolutePath() + File.pathSeparator + preferences.get("RECENT_FILES", ""));
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
                preferences.put("MOD_FC_INIT_DIR", file.getParentFile().getAbsolutePath());
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
                preferences.put("JSON_FC_INIT_DIR", file.getParentFile().getAbsolutePath());
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
                preferences.put("JSON_FC_INIT_DIR", file.getParentFile().getAbsolutePath());
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

    private void saveSection(File file) {
        if (file != null) {
            if (file.getParentFile().isDirectory()) {
                jsonFileChooser.setInitialDirectory(file.getParentFile());
                preferences.put("JSON_FC_INIT_DIR", file.getParentFile().getAbsolutePath());
                preferences.put("RECENT_FILES", file.getAbsolutePath() + File.pathSeparator + preferences.get("RECENT_FILES", ""));
            }
            try {
                storageManager.saveToJson(file, sectionManager.getSnapshot().get());
                setWindowFileTitle(file);
                dirtyAsterisk.set("");
            } catch (Exception e) {
                alertsFactory.incorrectFileAlert(e, getStage()).show();
            }
        }
    }

    @FXML
    private void submitCoords() {
        double x;
        double z;

        try {
            x = decimalFormat.parse(picketX.getText()).doubleValue();
            z = decimalFormat.parse(picketZ.getText()).doubleValue();
        } catch (ParseException e) {
            alertsFactory.simpleExceptionAlert(e, getStage()).show();
            return;
        }

        var violationsX = validator.validateValue(Picket.IMPL_CLASS, "offsetX", x);
        if (!violationsX.isEmpty()) {
            alertsFactory.violationsAlert(violationsX, getStage()).show();
            picketX.setText(String.valueOf(picket.get().getOffsetX()));
        } else {
            historyManager.performThenSnapshot(() -> sectionManager.updateX(picketIndex.get(), x));
        }

        var violationsZ = validator.validateValue(Picket.IMPL_CLASS, "z", z);
        if (!violationsZ.isEmpty()) {
            alertsFactory.violationsAlert(violationsZ, getStage()).show();
            picketZ.setText(String.valueOf(picket.get().getZ()));
        } else {
            historyManager.performThenSnapshot(() -> sectionManager.updateZ(picketIndex.get(), z));
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
