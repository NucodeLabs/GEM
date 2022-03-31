package ru.nucodelabs.gem.app;

import com.google.inject.Provider;
import com.google.inject.name.Named;
import jakarta.validation.Validator;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.gem.app.command.AddPicketCommand;
import ru.nucodelabs.gem.app.command.Command;
import ru.nucodelabs.gem.app.command.CommandExecutor;
import ru.nucodelabs.gem.app.command.PicketModificationCommand;
import ru.nucodelabs.gem.app.io.StorageManager;
import ru.nucodelabs.gem.view.AlertsFactory;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.Optional;

public class AppService {

    private final StorageManager storageManager;
    private final CommandExecutor commandExecutor;
    private final Validator validator;
    private final FileChooser jsonFileChooser;
    private final FileChooser expFileChooser;
    private final FileChooser modFileChooser;
    private final AlertsFactory alertsFactory;
    private final Provider<Dialog<ButtonType>> saveDialogProvider;
    private final ObservableList<Picket> picketObservableList;
    private final ObjectProperty<Picket> picket;
    private final IntegerProperty picketIndex;

    private Stage stage;

    @Inject
    private PicketModificationCommand.Factory picketModificationCommandFactory;
    @Inject
    private AddPicketCommand.Factory addPicketCommandFactory;

    @Inject
    public AppService(
            StorageManager storageManager,
            CommandExecutor commandExecutor, Validator validator,
            @Named("JSON") FileChooser jsonFileChooser,
            @Named("EXP") FileChooser expFileChooser,
            @Named("MOD") FileChooser modFileChooser,
            AlertsFactory alertsFactory,
            @Named("Save") Provider<Dialog<ButtonType>> saveDialogProvider,
            ObservableList<Picket> picketObservableList,
            ObjectProperty<Picket> picket,
            IntegerProperty picketIndex) {
        this.storageManager = storageManager;
        this.commandExecutor = commandExecutor;
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
            } catch (Exception e) {
                alertsFactory.incorrectFileAlert(e, stage).show();
            }
        }
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
        File file = jsonFileChooser.showSaveDialog(stage);
        if (file != null) {
            if (file.getParentFile().isDirectory()) {
                jsonFileChooser.setInitialDirectory(file.getParentFile());
            }
            try {
                storageManager.saveSectionToJsonFile(file, new Section(picketObservableList));
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
            execute(addPicketCommandFactory.create(picketFromEXPFile));
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
                execute(addPicketCommandFactory.create(loadedPicket));
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

                execute(picketModificationCommandFactory.create(newPicket));
            } catch (Exception e) {
                alertsFactory.incorrectFileAlert(e, stage).show();
            }
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
    }

    public void execute(Command command) {
        commandExecutor.execute(command);
    }

    public void undo() {
        commandExecutor.undo();
    }

    public void redo() {
        commandExecutor.redo();
    }
}
