package ru.nucodelabs.gem.view.main;

import com.google.inject.name.Named;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.core.utils.OSDetect;
import ru.nucodelabs.gem.dao.Section;
import ru.nucodelabs.gem.dao.SectionFactory;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.alerts.IncorrectFileAlert;
import ru.nucodelabs.gem.view.alerts.UnsafeDataAlert;

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
    private Section savedStateSection;
    @Inject
    private Provider<SectionFactory> sectionFactoryProvider;
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
    public MainViewController(
            ObjectProperty<Picket> picket,
            IntegerProperty picketIndex,
            ObservableList<Picket> picketObservableList,
            Section savedStateSection) {
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
        return !savedStateSection.getPickets().equals(picketObservableList);
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
                savedStateSection.loadFromJson(file);
            } catch (Exception e) {
                new IncorrectFileAlert(e, getStage()).show();
                return;
            }
            picketObservableList.setAll(savedStateSection.getPickets());
            picketIndex.set(0);
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
                Section newSectionState = sectionFactoryProvider.get().create(picketObservableList);
                newSectionState.saveToJson(file);
                savedStateSection = newSectionState.clone();
            } catch (Exception e) {
                new IncorrectFileAlert(e, getStage()).show();
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
                Picket newPicket =
                        sectionFactoryProvider
                                .get()
                                .create(picketObservableList)
                                .loadModelDataFromMODFile(picketIndex.get(), file);
                picket.set(newPicket);
            } catch (Exception e) {
                new IncorrectFileAlert(e, getStage()).show();
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
//        try {
//            Picket solvedPicket = new Picket(
//                    picket.get().name(),
//                    picket.get().experimentalData(),
//                    InverseSolver.getOptimizedPicket(picket.get())
//            );
//            picket.set(solvedPicket);
//        } catch (Exception e) {
//            new UnsafeDataAlert(picket.get().name(), getStage()).show();
//        }
    }

    private void addEXP(File file) {
        try {
            sectionFactoryProvider
                    .get()
                    .create(picketObservableList)
                    .loadExperimentalDataFromEXPFile(file);
        } catch (Exception e) {
            new IncorrectFileAlert(e, getStage()).show();
            return;
        }
        picketIndex.set(picketObservableList.size() - 1);
        compatibilityModeAlert();
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
            new UnsafeDataAlert(picket.get().name(), getStage()).show();
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
