package ru.nucodelabs.gem.view.main;

import com.google.inject.name.Named;
import jakarta.validation.Validator;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.math3.analysis.MultivariateFunction;
import ru.nucodelabs.algorithms.inverse_solver.InverseSolver;
import ru.nucodelabs.algorithms.inverse_solver.inverse_functions.FunctionValue;
import ru.nucodelabs.algorithms.inverse_solver.inverse_functions.SquaresDiff;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.gem.app.io.FileManager;
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

import static java.util.Objects.requireNonNull;

public class MainViewController extends Controller {

    private final StringProperty vesTitle = new SimpleStringProperty("");
    private final StringProperty vesNumber = new SimpleStringProperty("0/0");
    private final BooleanProperty noFileOpened = new SimpleBooleanProperty(true);

    private final ObjectProperty<Picket> picket;
    private final IntegerProperty picketIndex;
    private final ObservableList<Picket> picketObservableList;

    // ++++++++++++++ FOR DEBUG ++++++++++++++
    @FXML
    public TextField sideLength;
    @FXML
    public TextField relThreshold;
    @FXML
    public TextField absThreshold;
    // ++++++++++++++++++++++++++++++++++++++++
    @Inject
    FileManager fileManager;
    private List<Picket> savedStateSection;
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
    Validator validator;

    @Inject
    public MainViewController(
            ObjectProperty<Picket> picket,
            IntegerProperty picketIndex,
            ObservableList<Picket> picketObservableList,
            @Named("SavedState") List<Picket> savedStateSection) {
        this.picket = picket;
        this.picketIndex = picketIndex;
        this.picketObservableList = picketObservableList;
        this.savedStateSection = savedStateSection;
        picketObservableList.addListener((ListChangeListener<? super Picket>) c -> {
            if (c.next()) {
                // если был удален последний пикет в то время когда он отображался
                if (c.wasRemoved()
                        && c.getFrom() == c.getTo()
                        && c.getTo() == picketIndex.get()
                        && picketIndex.get() >= picketObservableList.size()) {
                    picketIndex.set(picketObservableList.size() - 1);
                }
                // если после изменения списка индекс не поменялся, но отображается не соответсвующий списку пикет
                if (picket.get() == null
                        || !picket.get().equals(picketObservableList.get(picketIndex.get()))) {
                    picket.set(picketObservableList.get(picketIndex.get()));
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
            if (picketObservableList.stream().noneMatch(p -> p.equals(newValue))) {
                picketObservableList.set(picketIndex.get(), picket.get());
            }
            update();
        });

        picketObservableList.setAll(List.copyOf(savedStateSection));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        uiProperties = requireNonNull(resources);

        if (OSDetect.isMacOS()) {
            CheckMenuItem useSystemMenu = new CheckMenuItem(uiProperties.getString("useSystemMenu"));
            menuView.getItems().add(0, useSystemMenu);
            useSystemMenu.selectedProperty().bindBidirectional(menuBar.useSystemMenuBarProperty());
        }

        getStage().setOnCloseRequest(e -> {
            if (isModified()) {
                Dialog<ButtonType> saveDialog = saveDialogProvider.get();
                saveDialog.initOwner(getStage());
                Optional<ButtonType> answer = saveDialog.showAndWait();
                if (answer.isPresent()) {
                    if (answer.get() == ButtonType.YES) {
                        saveSection();
                        e.consume();
                    } else if (answer.get() == ButtonType.CANCEL) {
                        e.consume();
                    }
                }
            }
        });
    }

    private boolean isModified() {
        return !savedStateSection.equals(picketObservableList);
    }

    @Override
    protected Stage getStage() {
        return root;
    }

    @FXML
    public void closeFile() {
        if (isModified()) {
            Dialog<ButtonType> saveDialog = saveDialogProvider.get();
            saveDialog.initOwner(getStage());
            Optional<ButtonType> answer = saveDialog.showAndWait();
            if (answer.isPresent()) {
                if (answer.get() == ButtonType.YES) {
                    saveSection();
                } else if (answer.get() == ButtonType.CANCEL) {
                    return;
                }
            }
        }
        getStage().close();
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
    public void openSection() {
        if (isModified()) {
            Dialog<ButtonType> saveDialog = saveDialogProvider.get();
            saveDialog.initOwner(getStage());
            Optional<ButtonType> answer = saveDialog.showAndWait();
            if (answer.isPresent()) {
                if (answer.get() == ButtonType.YES) {
                    saveSection();
                } else if (answer.get() == ButtonType.CANCEL) {
                    return;
                }
            }
        }
        File file = jsonFileChooser.showOpenDialog(getStage());
        if (file != null) {
            if (file.getParentFile().isDirectory()) {
                jsonFileChooser.setInitialDirectory(file.getParentFile());
            }

            try {
                List<Picket> loadedPickets = fileManager.loadSectionFromJsonFile(file);
                var violations =
                        validator.validate(new Section(loadedPickets));

                if (!violations.isEmpty()) {
                    alertsFactory.violationsAlert(violations, getStage()).show();
                    return;
                }

                picketObservableList.setAll(loadedPickets);
                savedStateSection = List.copyOf(loadedPickets);
                picketIndex.set(0);
            } catch (Exception e) {
                alertsFactory.incorrectFileAlert(e, getStage()).show();
            }
        }
    }

    @FXML
    public void saveSection() {
        File file = jsonFileChooser.showSaveDialog(getStage());
        if (file != null) {
            if (file.getParentFile().isDirectory()) {
                jsonFileChooser.setInitialDirectory(file.getParentFile());
            }
            try {
                fileManager.saveSectionToJsonFile(file, picketObservableList);
                savedStateSection = List.copyOf(picketObservableList);
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
                Picket newPicket = fileManager.loadModelDataFromMODFile(file, picket.get());

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
        //Размер симплекса (по каждому измерению)
        double SIDE_LENGTH = 0.1;

        //Какие-то константы для SimplexOptimize
        double RELATIVE_THRESHOLD = 1e-10;
        double ABSOLUTE_THRESHOLD = 1e-30;

        if (!sideLength.getText().isBlank()) {
            try {
                SIDE_LENGTH = Double.parseDouble(sideLength.getText());
            } catch (NumberFormatException e) {
                alertsFactory.simpleExceptionAlert(e).show();
            }
        }

        if (!relThreshold.getText().isBlank()) {
            try {
                RELATIVE_THRESHOLD = Double.parseDouble(relThreshold.getText());
            } catch (NumberFormatException e) {
                alertsFactory.simpleExceptionAlert(e).show();
            }
        }

        if (!absThreshold.getText().isBlank()) {
            try {
                ABSOLUTE_THRESHOLD = Double.parseDouble(absThreshold.getText());
            } catch (NumberFormatException e) {
                alertsFactory.simpleExceptionAlert(e).show();
            }
        }

        MultivariateFunction multivariateFunction = new FunctionValue(
                picket.get().experimentalData(),
                new SquaresDiff()
        );

        InverseSolver inverseSolver = new InverseSolver(
                picket.get(),
                SIDE_LENGTH,
                RELATIVE_THRESHOLD,
                ABSOLUTE_THRESHOLD,
                multivariateFunction
        );

        try {
            Picket newPicket = new Picket(
                    picket.get().name(),
                    picket.get().experimentalData(),
                    inverseSolver.getOptimizedPicket()
            );
            picket.set(newPicket);
        } catch (Exception e) {
            alertsFactory.unsafeDataAlert(picket.get().name(), getStage()).show();
        }
    }

    private void addEXP(File file) {
        try {
            Picket picketFromEXPFile = fileManager.loadPicketFromEXPFile(file);
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
        if (!picketObservableList.isEmpty()) {
            noFileOpened.set(false);
            noFileScreenController.hide();
        }
        updateVESText();
        updateVESNumber();
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
