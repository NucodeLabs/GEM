package ru.nucodelabs.gem.app;

import com.google.inject.Provider;
import com.google.inject.name.Named;
import jakarta.validation.Validator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.gem.app.io.StorageManager;
import ru.nucodelabs.gem.view.AlertsFactory;

import javax.inject.Inject;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AppManager {

    private final StorageManager storageManager;
    private final HistoryManager historyManager;
    private final SectionManager sectionManager;
    private final Validator validator;
    private final FileChooser jsonFileChooser;
    private final FileChooser expFileChooser;
    private final FileChooser modFileChooser;
    private final AlertsFactory alertsFactory;
    private final Provider<Dialog<ButtonType>> saveDialogProvider;
    private final IntegerProperty picketIndex;
    private final StringProperty windowTitle = new SimpleStringProperty("GEM");
    private final StringProperty dirtyAsterisk = new SimpleStringProperty("");

    private Stage stage;

    @Inject
    public AppManager(
            StorageManager storageManager,
            HistoryManager historyManager,
            SectionManager sectionManager,
            Validator validator,
            @Named("JSON") FileChooser jsonFileChooser,
            @Named("EXP") FileChooser expFileChooser,
            @Named("MOD") FileChooser modFileChooser,
            AlertsFactory alertsFactory,
            @Named("Save") Provider<Dialog<ButtonType>> saveDialogProvider,
            IntegerProperty picketIndex) {
        this.storageManager = storageManager;
        this.historyManager = historyManager;
        this.sectionManager = sectionManager;
        this.jsonFileChooser = jsonFileChooser;
        this.validator = validator;
        this.expFileChooser = expFileChooser;
        this.modFileChooser = modFileChooser;
        this.alertsFactory = alertsFactory;
        this.saveDialogProvider = saveDialogProvider;
        this.picketIndex = picketIndex;
    }

    public void init(Stage stage) {
        this.stage = stage;
        sectionManager.subscribe(evt -> {
            if (!storageManager.compareWithSavedState(sectionManager.getSnapshot())) {
                dirtyAsterisk.set("*");
            } else {
                dirtyAsterisk.set("");
            }
        });
        stage.titleProperty().bind(Bindings.concat(dirtyAsterisk, windowTitle));
    }

    public void openSection(Event event) {
        if (askToSave(event).isConsumed()) {
            return;
        }
        File file = jsonFileChooser.showOpenDialog(stage);
        if (file != null) {
            if (file.getParentFile().isDirectory()) {
                jsonFileChooser.setInitialDirectory(file.getParentFile());
            }

            try {
                Section loadedSection = storageManager.loadSectionFromJsonFile(file);
                var violations =
                        validator.validate(loadedSection);

                if (!violations.isEmpty()) {
                    alertsFactory.violationsAlert(violations, stage).show();
                    return;
                }

                sectionManager.setSection(loadedSection);
                picketIndex.set(0);
                historyManager.clear();
                historyManager.snapshot();
                setWindowFileTitle(file);
            } catch (Exception e) {
                alertsFactory.incorrectFileAlert(e, stage).show();
            }
        }
    }

    private void setWindowFileTitle(File file) {
        windowTitle.set(file.getName());
    }

    private void resetWindowTitle() {
        windowTitle.set("GEM");
    }

    public Event askToSave(Event event) {
        if (!storageManager.compareWithSavedState(sectionManager.getSnapshot())) {
            Dialog<ButtonType> saveDialog = saveDialogProvider.get();
            saveDialog.initOwner(stage);
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

    public void saveSection() {
        File file;
        if (storageManager.getSavedStateFile() == null) {
            file = jsonFileChooser.showSaveDialog(stage);
        } else {
            file = storageManager.getSavedStateFile();
        }
        saveSection(file);
    }

    public void saveSectionAs() {
        saveSection(jsonFileChooser.showSaveDialog(stage));
    }

    private void saveSection(File file) {
        if (file != null) {
            if (file.getParentFile().isDirectory()) {
                jsonFileChooser.setInitialDirectory(file.getParentFile());
            }
            try {
                storageManager.saveSectionToJsonFile(file, sectionManager.getSnapshot());
                setWindowFileTitle(file);
                dirtyAsterisk.set("");
            } catch (Exception e) {
                alertsFactory.incorrectFileAlert(e, stage).show();
            }
        }
    }

    public void importEXP() {
        List<File> files = expFileChooser.showOpenMultipleDialog(stage);
        if (files != null) {
            if (files.get(files.size() - 1).getParentFile().isDirectory()) {
                expFileChooser.setInitialDirectory(files.get(files.size() - 1).getParentFile());
            }
            for (var file : files) {
                addEXP(file);
            }
        }
    }

    private void addEXP(File file) {
        try {
            Picket picketFromEXPFile = storageManager.loadPicketFromEXPFile(file);
            var violations = validator.validate(picketFromEXPFile);
            if (!violations.isEmpty()) {
                alertsFactory.violationsAlert(violations, stage).show();
                return;
            }
            historyManager.performThenSnapshot(() -> sectionManager.add(picketFromEXPFile));
            picketIndex.set(sectionManager.size() - 1);
            compatibilityModeAlert();
        } catch (Exception e) {
            alertsFactory.incorrectFileAlert(e, stage).show();
        }
    }

    public void exportJsonPicket() {
        File file = jsonFileChooser.showSaveDialog(stage);

        if (file != null) {
            if (file.getParentFile().isDirectory()) {
                jsonFileChooser.setInitialDirectory(file.getParentFile());
            }

            try {
                storageManager.savePicketToJsonFile(file, sectionManager.get(picketIndex.get()));
            } catch (Exception e) {
                alertsFactory.simpleExceptionAlert(e, stage).show();
            }
        }
    }

    public void importJsonPicket() {
        File file = jsonFileChooser.showOpenDialog(stage);

        if (file != null) {
            if (file.getParentFile().isDirectory()) {
                jsonFileChooser.setInitialDirectory(file.getParentFile());
            }

            try {
                Picket loadedPicket = storageManager.loadPicketFromJsonFile(file);
                historyManager.performThenSnapshot(() -> sectionManager.add(loadedPicket));
                picketIndex.set(sectionManager.size() - 1);
            } catch (Exception e) {
                alertsFactory.incorrectFileAlert(e, stage).show();
            }
        }
    }

    public void importMOD() {
        File file = modFileChooser.showOpenDialog(stage);

        if (file != null) {
            if (file.getParentFile().isDirectory()) {
                modFileChooser.setInitialDirectory(file.getParentFile());
            }
            try {
                Picket newPicket = storageManager.loadModelDataFromMODFile(file, sectionManager.get(picketIndex.get()));

                var violations = validator.validate(newPicket);

                if (!violations.isEmpty()) {
                    alertsFactory.violationsAlert(violations, stage);
                    return;
                }

                historyManager.performThenSnapshot(() -> sectionManager.updatePicket(picketIndex.get(), newPicket));
            } catch (Exception e) {
                alertsFactory.incorrectFileAlert(e, stage).show();
            }
        }
    }

    private void compatibilityModeAlert() {
        if (sectionManager.get(picketIndex.get()) != null
                && sectionManager.get(picketIndex.get()).experimentalData().isUnsafe()) {
            alertsFactory.unsafeDataAlert(sectionManager.get(picketIndex.get()).name(), stage).show();
        }
    }

    public void closeFile(Event event) {
        if (askToSave(event).isConsumed()) {
            return;
        }
        sectionManager.setSection(new Section(Collections.emptyList()));
        storageManager.clearSavedState();
        historyManager.clear();
        resetWindowTitle();
    }
}
