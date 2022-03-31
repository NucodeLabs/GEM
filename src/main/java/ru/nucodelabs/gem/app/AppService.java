package ru.nucodelabs.gem.app;

import com.google.inject.Provider;
import com.google.inject.name.Named;
import jakarta.validation.Validator;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.nucodelabs.algorithms.inverse_solver.InverseSolver;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.gem.app.annotation.Subject;
import ru.nucodelabs.gem.app.io.StorageManager;
import ru.nucodelabs.gem.app.operation.AddPicketOperation;
import ru.nucodelabs.gem.app.operation.Operation;
import ru.nucodelabs.gem.app.operation.OperationExecutor;
import ru.nucodelabs.gem.app.operation.PicketModificationOperation;
import ru.nucodelabs.gem.view.AlertsFactory;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.Optional;

public class AppService {

    private final StorageManager storageManager;
    private final OperationExecutor operationExecutor;
    private final Validator validator;
    private final FileChooser jsonFileChooser;
    private final FileChooser expFileChooser;
    private final FileChooser modFileChooser;
    private final AlertsFactory alertsFactory;
    private final Provider<Dialog<ButtonType>> saveDialogProvider;
    private final ObservableList<Picket> picketObservableList;
    private final ObservableObjectValue<Picket> picket;
    private final IntegerProperty picketIndex;

    private Stage stage;

    @Inject
    private PicketModificationOperation.Factory picketModificationOperationFactory;
    @Inject
    private AddPicketOperation.Factory addPicketOperationFactory;

    @Inject
    public AppService(
            StorageManager storageManager,
            OperationExecutor operationExecutor, Validator validator,
            @Named("JSON") FileChooser jsonFileChooser,
            @Named("EXP") FileChooser expFileChooser,
            @Named("MOD") FileChooser modFileChooser,
            AlertsFactory alertsFactory,
            @Named("Save") Provider<Dialog<ButtonType>> saveDialogProvider,
            @Subject ObservableList<Picket> picketObservableList,
            ObservableObjectValue<Picket> picket,
            IntegerProperty picketIndex) {
        this.storageManager = storageManager;
        this.operationExecutor = operationExecutor;
        this.jsonFileChooser = jsonFileChooser;
        this.validator = validator;
        this.expFileChooser = expFileChooser;
        this.modFileChooser = modFileChooser;
        this.alertsFactory = alertsFactory;
        this.saveDialogProvider = saveDialogProvider;
        this.picketObservableList = picketObservableList;
        this.picket = picket;
        this.picketIndex = picketIndex;

        this.picketObservableList.setAll(storageManager.getSavedState().pickets());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        picketObservableList.addListener((ListChangeListener<? super Picket>) c -> {
            if (c.next()) {
                if (!storageManager.compareWithSavedState(new Section(picketObservableList))) {
                    stage.setTitle(stage.getTitle().indexOf("*") == 0 ?
                            stage.getTitle() :
                            "*" + stage.getTitle());
                } else {
                    stage.setTitle(stage.getTitle().indexOf("*") == 0 ?
                            stage.getTitle().substring(1) :
                            stage.getTitle());
                }
            }
        });
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

                picketObservableList.setAll(loadedSection.pickets());
                picketIndex.set(0);
                operationExecutor.clearHistory();
                setWindowFileTitle(file);
            } catch (Exception e) {
                alertsFactory.incorrectFileAlert(e, stage).show();
            }
        }
    }

    private void setWindowFileTitle(File file) {
        stage.setTitle(file.getName());
    }

    private void resetWindowTitle() {
        stage.setTitle("GEM");
    }

    public Event askToSave(Event event) {
        if (!storageManager.compareWithSavedState(new Section(picketObservableList))) {
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
                storageManager.saveSectionToJsonFile(file, new Section(picketObservableList));
                setWindowFileTitle(file);
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
            var violations = validator.validate(picket);
            if (!violations.isEmpty()) {
                alertsFactory.violationsAlert(violations, stage).show();
                return;
            }
            execute(addPicketOperationFactory.create(picketFromEXPFile));
            picketIndex.set(picketObservableList.size() - 1);
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
                storageManager.savePicketToJsonFile(file, picket.get());
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
                execute(addPicketOperationFactory.create(loadedPicket));
                picketIndex.set(picketObservableList.size() - 1);
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
                Picket newPicket = storageManager.loadModelDataFromMODFile(file, picket.get());

                var violations = validator.validate(newPicket);

                if (!violations.isEmpty()) {
                    alertsFactory.violationsAlert(violations, stage);
                    return;
                }

                execute(picketModificationOperationFactory.create(newPicket));
            } catch (Exception e) {
                alertsFactory.incorrectFileAlert(e, stage).show();
            }
        }
    }

    public void inverseSolve() {
        InverseSolver inverseSolver = new InverseSolver(picket.get());

        try {
            execute(picketModificationOperationFactory.create(inverseSolver.getOptimizedModelData()));
        } catch (Exception e) {
            alertsFactory.simpleExceptionAlert(e, stage).show();
        }
    }

    private void compatibilityModeAlert() {
        if (picket.get() != null && picket.get().experimentalData().isUnsafe()) {
            alertsFactory.unsafeDataAlert(picket.get().name(), stage).show();
        }
    }

    public void closeFile(Event event) {
        if (askToSave(event).isConsumed()) {
            return;
        }
        picketObservableList.clear();
        storageManager.clearSavedState();
        resetWindowTitle();
    }

    public void execute(Operation operation) {
        operationExecutor.execute(operation);
    }

    public void undo() {
        operationExecutor.undo();
    }

    public void redo() {
        operationExecutor.redo();
    }
}
