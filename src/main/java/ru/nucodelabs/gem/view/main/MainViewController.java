package ru.nucodelabs.gem.view.main;

import io.reactivex.rxjava3.subjects.Subject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import ru.nucodelabs.algorithms.inverseSolver.InverseSolver;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.core.utils.OSDetect;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.alerts.ExceptionAlert;
import ru.nucodelabs.gem.view.alerts.IncorrectFileAlert;
import ru.nucodelabs.gem.view.alerts.UnsafeDataAlert;
import ru.nucodelabs.gem.view.filechoosers.EXPFileChooserFactory;
import ru.nucodelabs.gem.view.filechoosers.JsonFileChooserFactory;
import ru.nucodelabs.gem.view.filechoosers.MODFileChooserFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;

public class MainViewController implements Controller {

    private final Subject<Section> sectionSubject;
    private final Subject<Picket> picketSubject;
    private ResourceBundle uiProperties;

    @Inject
    private Section section;
    private Picket picket;

    /**
     * Properties
     */
    private final StringProperty vesTitle = new SimpleStringProperty("");
    private final StringProperty vesNumber = new SimpleStringProperty("0/0");
    private final BooleanProperty noFileOpened = new SimpleBooleanProperty(true);

    @Inject
    public MainViewController(
            Subject<Section> sectionSubject,
            Subject<Picket> picketSubject) {
        this.sectionSubject = sectionSubject;
        this.picketSubject = picketSubject;
        sectionSubject
                .subscribe(section1 -> {
                    section = section1;
                    update();
                });
        picketSubject
                .subscribe(picket1 -> {
                    picket = picket1;
                    update();
                });
    }

    @FXML
    public Stage root;
    @FXML
    public MenuBar menuBar;
    @FXML
    public Menu menuView;
    @FXML
    public NoFileScreenController noFileScreenController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        uiProperties = requireNonNull(resources);

        if (OSDetect.isMacOS()) {
            CheckMenuItem useSystemMenu = new CheckMenuItem(uiProperties.getString("useSystemMenu"));
            menuView.getItems().add(0, useSystemMenu);
            useSystemMenu.selectedProperty().bindBidirectional(menuBar.useSystemMenuBarProperty());
        }
    }

    @Override
    public Stage getStage() {
        return root;
    }

    @FXML
    public void closeFile() {
        getStage().close();
    }

    /**
     * Asks which EXP files and then imports them to current window
     */
    @FXML
    public void importEXP() {
        List<File> files = new EXPFileChooserFactory().create().showOpenMultipleDialog(getStage());
        if (files != null && files.size() != 0) {
            for (var file : files) {
                addEXP(file);
            }
        }
    }

    @FXML
    public void openSection() {
        File file = new JsonFileChooserFactory().create().showOpenDialog(getStage());
        if (file != null) {
            try {
                section.loadFromJson(file);
            } catch (Exception e) {
                new IncorrectFileAlert(e, getStage()).show();
                return;
            }
            picketSubject.onNext(section.getLastPicket());
            sectionSubject.onNext(section);
        }
    }

    @FXML
    public void saveSection() {
        File file = new JsonFileChooserFactory().create().showSaveDialog(getStage());
        if (file != null) {
            try {
                section.saveToJson(file);
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
        try {
            new MainViewFactory().create().show();
        } catch (IOException e) {
            new ExceptionAlert(e).show();
        }
    }

    /**
     * Asks which file to import and then import it
     */
    @FXML
    public void importMOD() {
        File file = new MODFileChooserFactory().create().showOpenDialog(getStage());

        if (file == null) {
            return;
        }

        try {
            Picket newPicket = section.loadModelDataFromMODFile(picketIndex(), file);
            picketSubject.onNext(newPicket);
        } catch (Exception e) {
            new IncorrectFileAlert(e, getStage()).show();
        }
    }

    @FXML
    public void switchToNextPicket() {
        if (section.getPicketsCount() > picketIndex() + 1) {
            picketSubject.onNext(section.getPicket(picketIndex() + 1));
        }
    }

    private int picketIndex() {
        return section.getPickets().indexOf(picket);
    }

    @FXML
    public void switchToPrevPicket() {
        if (picketIndex() > 0 && section.getPicketsCount() > 0) {
            picketSubject.onNext(section.getPicket(picketIndex() - 1));
        }
    }

    @FXML
    public void inverseSolve() {
        try {
            Picket solvedPicket = section.setModelData(picketIndex(),
                    InverseSolver.getOptimizedPicket(section.getPicket(picketIndex())));
            picketSubject.onNext(solvedPicket);
        } catch (Exception e) {
            new UnsafeDataAlert(section.getName(picketIndex()), getStage()).show();
        }
    }

    private void addEXP(File file) {
        try {
            section.loadExperimentalDataFromEXPFile(section.getPicketsCount(), file);
        } catch (Exception e) {
            new IncorrectFileAlert(e, getStage()).show();
            return;
        }
        picketSubject.onNext(section.getLastPicket());
        sectionSubject.onNext(section);
        compatibilityModeAlert();
    }

    /**
     * Adds files names to vesText
     */
    private void updateVESText() {
        vesTitle.set(section.getName(picketIndex()));
    }

    private void updateVESNumber() {
        vesNumber.set(picketIndex() + 1 + "/" + section.getPicketsCount());
    }

    /**
     * Warns about compatibility mode if data is unsafe
     */
    private void compatibilityModeAlert() {
        ExperimentalData experimentalData = section.getPicket(picketIndex()).experimentalData();
        if (experimentalData.isUnsafe()) {
            new UnsafeDataAlert(section.getName(picketIndex()), getStage()).show();
        }
    }

    protected void update() {
        if (section.getPicketsCount() > 0) {
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
