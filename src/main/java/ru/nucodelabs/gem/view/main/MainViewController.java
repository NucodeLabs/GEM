package ru.nucodelabs.gem.view.main;

import com.google.common.eventbus.EventBus;
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
import ru.nucodelabs.gem.core.events.NewWindowRequest;
import ru.nucodelabs.gem.core.events.PicketSwitchEvent;
import ru.nucodelabs.gem.core.events.SectionChangeEvent;
import ru.nucodelabs.gem.core.utils.OSDetect;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.view.AbstractSectionController;
import ru.nucodelabs.gem.view.alerts.IncorrectFileAlert;
import ru.nucodelabs.gem.view.alerts.UnsafeDataAlert;
import ru.nucodelabs.gem.view.charts.MisfitStacksController;
import ru.nucodelabs.gem.view.charts.VESCurvesController;
import ru.nucodelabs.gem.view.filechoosers.EXPFileChooserFactory;
import ru.nucodelabs.gem.view.filechoosers.JsonFileChooserFactory;
import ru.nucodelabs.gem.view.filechoosers.MODFileChooserFactory;
import ru.nucodelabs.gem.view.tables.ExperimentalTableController;
import ru.nucodelabs.gem.view.tables.ModelTableController;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;

public class MainViewController extends AbstractSectionController {

    /**
     * Service-objects
     */
    private final EventBus appEvents;
    private ResourceBundle uiProperties;

    /**
     * Properties
     */
    private final StringProperty vesTitle;
    private final StringProperty vesNumber;
    private final BooleanProperty noFileOpened;

    /**
     * Initialization
     *
     * @param appEvents event bus
     * @param section   VES Data
     */
    public MainViewController(EventBus appEvents, Section section) {
        this.appEvents = requireNonNull(appEvents);
        this.section = requireNonNull(section);
        viewEvents = new EventBus();
        viewEvents.register(this);

        currentPicket = -1;
        noFileOpened = new SimpleBooleanProperty(true);
        vesTitle = new SimpleStringProperty("");

        vesNumber = new SimpleStringProperty("0/0");
    }

    @FXML
    public Stage root;
    @FXML
    public VESCurvesController vesCurvesController;
    @FXML
    public MenuBar menuBar;
    @FXML
    public Menu menuView;
    @FXML
    public PicketsBarController picketsBarController;
    @FXML
    public NoFileScreenController noFileScreenController;
    @FXML
    public MisfitStacksController misfitStacksController;
    @FXML
    public ModelTableController modelTableController;
    @FXML
    public ExperimentalTableController experimentalTableController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initPicketsBarController();
        initNoFileScreenController();
        initMisfitStacksController();
        initVESCurvesController();
        initModelTableController();
        initExperimentalTableController();

        uiProperties = requireNonNull(resources);

        if (OSDetect.isMacOS()) {
            CheckMenuItem useSystemMenu = new CheckMenuItem(uiProperties.getString("useSystemMenu"));
            menuView.getItems().add(0, useSystemMenu);
            useSystemMenu.selectedProperty().bindBidirectional(menuBar.useSystemMenuBarProperty());
        }
    }

    private void initNoFileScreenController() {
        noFileScreenController.setImportEXPAction(this::importEXP);
        noFileScreenController.setOpenSectionAction(this::openSection);
    }

    private void initMisfitStacksController() {
        misfitStacksController.setSection(section);
        misfitStacksController.setEventBus(viewEvents);
        viewEvents.register(misfitStacksController);
    }

    private void initVESCurvesController() {
        vesCurvesController.setSection(section);
        vesCurvesController.setEventBus(viewEvents);
        viewEvents.register(vesCurvesController);
    }

    private void initModelTableController() {
        modelTableController.setSection(section);
        modelTableController.setViewEvents(viewEvents);
        viewEvents.register(modelTableController);
    }

    private void initExperimentalTableController() {
        experimentalTableController.setSection(section);
        experimentalTableController.setViewEvents(viewEvents);
        viewEvents.register(experimentalTableController);
    }

    private void initPicketsBarController() {
        picketsBarController.setSection(section);
        picketsBarController.setViewEvents(viewEvents);
        viewEvents.register(picketsBarController);
    }

    @Override
    protected Stage getStage() {
        return root;
    }

    /**
     * <h1>Event Handlers</h1>
     */

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
            viewEvents.post(new PicketSwitchEvent(0));
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
        appEvents.post(new NewWindowRequest());
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
            section.loadModelDataFromMODFile(currentPicket, file);
        } catch (Exception e) {
            new IncorrectFileAlert(e, getStage()).show();
        }
    }

    @FXML
    public void switchToNextPicket() {
        if (section.getPicketsCount() > currentPicket + 1) {
            viewEvents.post(new PicketSwitchEvent(currentPicket + 1));
        }
    }

    @FXML
    public void switchToPrevPicket() {
        if (currentPicket > 0 && section.getPicketsCount() > 0) {
            viewEvents.post(new PicketSwitchEvent(currentPicket - 1));
        }
    }

    @FXML
    public void inverseSolve() {
        section.setModelData(currentPicket,
                InverseSolver.getOptimizedPicket(section.getPicket(currentPicket)));
        viewEvents.post(new SectionChangeEvent());
    }

    private void addEXP(File file) {
        try {
            section.loadExperimentalDataFromEXPFile(currentPicket + 1, file);
        } catch (Exception e) {
            new IncorrectFileAlert(e, getStage()).show();
            return;
        }
        viewEvents.post(new PicketSwitchEvent(currentPicket++));
        compatibilityModeAlert();
    }

    /**
     * Adds files names to vesText
     */
    private void updateVESText() {
        vesTitle.set(section.getName(currentPicket));
    }

    private void updateVESNumber() {
        vesNumber.set(currentPicket + 1 + "/" + section.getPicketsCount());
    }

    /**
     * Warns about compatibility mode if data is unsafe
     */
    private void compatibilityModeAlert() {
        ExperimentalData experimentalData = section.getPicket(currentPicket).experimentalData();
        if (experimentalData.isUnsafe()) {
            new UnsafeDataAlert(section.getName(currentPicket), getStage()).show();
        }
    }

    @Override
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
