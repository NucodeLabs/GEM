package ru.nucodelabs.gem.view.main;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.gem.core.ViewService;
import ru.nucodelabs.gem.core.events.ModificationType;
import ru.nucodelabs.gem.core.events.UpdateViewEvent;
import ru.nucodelabs.gem.core.utils.OSDetect;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.charts.MisfitStacksController;
import ru.nucodelabs.gem.view.charts.VESCurvesController;
import ru.nucodelabs.gem.view.tables.ExperimentalTableController;
import ru.nucodelabs.gem.view.tables.ModelTableController;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;

public class MainViewController extends Controller {

    /**
     * Service-objects
     */
    private final ViewService viewService;
    private final EventBus eventBus;
    private ResourceBundle uiProperties;

    /**
     * Properties
     */
    private final StringProperty vesTitle;
    private final StringProperty vesNumber;
    private final BooleanProperty noFileOpened;
    private int currentPicket;

    /**
     * Data models
     */
    private final Section section;

    /**
     * Initialization
     *
     * @param viewService View Manager
     * @param eventBus    event bus
     * @param section     VES Data
     */
    public MainViewController(ViewService viewService, EventBus eventBus, Section section) {
        this.viewService = requireNonNull(viewService);
        this.eventBus = eventBus;
        this.section = requireNonNull(section);

        eventBus.register(this);

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

    private void initMisfitStacksController() {
        misfitStacksController.setSection(section);
    }

    private void initNoFileScreenController() {
        noFileScreenController.setImportEXPAction(this::importEXP);
        noFileScreenController.setOpenSectionAction(this::openSection);
    }

    private void initVESCurvesController() {
        vesCurvesController.setSection(section);
    }

    private void initModelTableController() {
        modelTableController.setSection(section);
    }

    private void initExperimentalTableController() {
        experimentalTableController.setSection(section);
    }

    private void initPicketsBarController() {
        picketsBarController.setSection(section);
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
        List<File> files = viewService.showOpenEXPFileChooser(getStage());
        if (files != null && files.size() != 0) {
            for (var file : files) {
                addEXP(file);
            }
        }
    }

    @FXML
    public void openSection() {
        File file = viewService.showOpenJsonFileChooser(getStage());
        if (file != null) {
            try {
                section.loadFromJson(file);
            } catch (Exception e) {
                viewService.alertIncorrectFile(getStage(), e);
                return;
            }
            currentPicket = 0;
            updateAll();
        }
    }

    @FXML
    public void saveSection() {
        File file = viewService.showSaveJsonFileChooser(getStage());
        if (file != null) {
            try {
                section.saveToJson(file);
            } catch (Exception e) {
                viewService.alertIncorrectFile(getStage(), e);
            }
        }
    }

    /**
     * Opens new window
     */
    @FXML
    public void newWindow() {
        viewService.start();
    }

    /**
     * Asks which file to import and then import it
     */
    @FXML
    public void importMOD() {
        File file = viewService.showOpenMODFileChooser(getStage());

        if (file == null) {
            return;
        }

        try {
            section.loadModelDataFromMODFile(currentPicket, file);
        } catch (Exception e) {
            viewService.alertIncorrectFile(getStage(), e);
        }

        updateAll();
    }

    @FXML
    public void switchToNextPicket() {
        if (section.getPicketsCount() > currentPicket + 1) {
            currentPicket++;
            updateAll();
        }
    }

    @FXML
    public void switchToPrevPicket() {
        if (currentPicket > 0 && section.getPicketsCount() > 0) {
            currentPicket--;
            updateAll();
        }
    }

    @Subscribe
    public void handleUpdateViewEvent(UpdateViewEvent event) {
        if (event.type() == ModificationType.MODEL_CURVE_DRAGGED) {
            updateOnDrag();
        }
        if (event.type() == ModificationType.PICKETS_BAR_CHANGE) {
            currentPicket = picketsBarController.getCurrentPicket();
            updateOnPicketsBarChange();
        }
    }

    private void addEXP(File file) {
        try {
            section.loadExperimentalDataFromEXPFile(currentPicket + 1, file);
        } catch (Exception e) {
            viewService.alertIncorrectFile(getStage(), e);
            return;
        }
        currentPicket++;
        compatibilityModeAlert();
        updateAll();
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
            viewService.alertExperimentalDataIsUnsafe(getStage(), section.getPicket(currentPicket).name());
        }
    }

    private void updateOnPicketsBarChange() {
        if (currentPicket >= section.getPicketsCount()) {
            currentPicket = section.getPicketsCount() - 1;
        }
        updateAll();
    }

    private void updateOnDrag() {
        vesCurvesController.updateTheoreticalCurve(currentPicket);
        misfitStacksController.update(currentPicket);
        modelTableController.update(currentPicket);
    }

    private void setCurrentPicket(int currentPicket) {
        this.currentPicket = currentPicket;
        updateAll();
    }

    private void updateAll() {
        if (section.getPicketsCount() > 0) {
            noFileOpened.set(false);
            noFileScreenController.hide();
        }
        picketsBarController.update();
        experimentalTableController.update(currentPicket);
        vesCurvesController.updateAll(currentPicket);
        modelTableController.update(currentPicket);
        misfitStacksController.update(currentPicket);
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
