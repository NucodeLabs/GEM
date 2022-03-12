package ru.nucodelabs.gem.view.main;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ExperimentalTableLine;
import ru.nucodelabs.data.ves.ModelTableLine;
import ru.nucodelabs.gem.core.ViewService;
import ru.nucodelabs.gem.core.utils.OSDetect;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.VESTablesConverters;
import ru.nucodelabs.gem.view.charts.MisfitStacksController;
import ru.nucodelabs.gem.view.charts.VESCurvesController;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;

public class MainViewController extends Controller implements Initializable {

    /**
     * Service-objects
     */
    private final ViewService viewService;

    private ResourceBundle uiProperties;

    /**
     * Properties
     */
    private final StringProperty vesTitle;
    private final StringProperty vesNumber;
    private final ObjectProperty<ObservableList<ExperimentalTableLine>> expTableData;
    private final ObjectProperty<ObservableList<ModelTableLine>> modelTableData;
    private final BooleanProperty noFileOpened;
    private final IntegerProperty currentPicket;

    /**
     * Data models
     */
    private final Section section;

    /**
     * Initialization
     *
     * @param viewService View Manager
     * @param section     VES Data
     */
    public MainViewController(ViewService viewService, Section section) {
        this.viewService = requireNonNull(viewService);
        this.section = requireNonNull(section);

        currentPicket = new SimpleIntegerProperty(-1);

        noFileOpened = new SimpleBooleanProperty(true);

        vesTitle = new SimpleStringProperty("");

        expTableData = new SimpleObjectProperty<>(FXCollections.observableList(new ArrayList<>()));
        modelTableData = new SimpleObjectProperty<>(FXCollections.observableList(new ArrayList<>()));
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
    public NoFileScreenController noFileScreenController;
    @FXML
    public MisfitStacksController misfitStacksController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initNoFileScreenController();
        initMisfitStacksController();
        initVESCurvesController();

        uiProperties = requireNonNull(resources);

        if (OSDetect.isMacOS()) {
            CheckMenuItem useSystemMenu = new CheckMenuItem(uiProperties.getString("useSystemMenu"));
            menuView.getItems().add(0, useSystemMenu);
            useSystemMenu.selectedProperty().bindBidirectional(menuBar.useSystemMenuBarProperty());
        }
    }

    private void initMisfitStacksController() {
        misfitStacksController.setCurrentPicketProperty(currentPicket);
        misfitStacksController.setSection(section);
    }

    private void initNoFileScreenController() {
        noFileScreenController.setImportEXPAction(this::importEXP);
        noFileScreenController.setOpenSectionAction(this::openSection);
    }

    private void initVESCurvesController() {
        vesCurvesController.setSection(section);
        vesCurvesController.setCurrentPicketProperty(currentPicket);
        vesCurvesController.setUpdateView(this::updateOnDrag);
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
            currentPicket.set(0);
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
            section.loadModelDataFromMODFile(currentPicket.get(), file);
        } catch (Exception e) {
            viewService.alertIncorrectFile(getStage(), e);
        }

        updateAll();
    }

    @FXML
    public void switchToNextPicket() {
        if (section.getPicketsCount() > currentPicket.get() + 1) {
            currentPicket.set(currentPicket.get() + 1);
            updateAll();
        }
    }

    @FXML
    public void switchToPrevPicket() {
        if (currentPicket.get() > 0 && section.getPicketsCount() > 0) {
            currentPicket.set(currentPicket.get() - 1);
            updateAll();
        }
    }

    private void addEXP(File file) {
        try {
            section.loadExperimentalDataFromEXPFile(currentPicket.get() + 1, file);
        } catch (Exception e) {
            viewService.alertIncorrectFile(getStage(), e);
            return;
        }
        currentPicket.set(currentPicket.get() + 1);
        compatibilityModeAlert();
        updateAll();
    }

    /**
     * Adds files names to vesText
     */
    private void updateVESText() {
        vesTitle.set(section.getName(currentPicket.get()));
    }

    private void updateVESNumber() {
        vesNumber.set(currentPicket.get() + 1 + "/" + section.getPicketsCount());
    }

    /**
     * Warns about compatibility mode if data is unsafe
     */
    private void compatibilityModeAlert() {
        ExperimentalData experimentalData = section.getPicket(currentPicket.get()).experimentalData();
        if (experimentalData.isUnsafe()) {
            viewService.alertExperimentalDataIsUnsafe(getStage(), section.getPicket(currentPicket.get()).name());
        }
    }

    private void updateOnDrag() {
        vesCurvesController.updateTheoreticalCurve();
        misfitStacksController.updateMisfitStacks();
        updateModelTable();
    }

    private void updateAll() {
        if (section.getPicketsCount() > 0) {
            noFileOpened.set(false);
            noFileScreenController.hide();
        }
        updateExpTable();
        vesCurvesController.updateExpCurves();
        vesCurvesController.updateTheoreticalCurve();
        vesCurvesController.updateModelCurve();
        updateModelTable();
        misfitStacksController.updateMisfitStacks();
        updateVESText();
        updateVESNumber();
    }

    private void updateExpTable() {
        expTableData.setValue(
                VESTablesConverters.toExperimentalTableData(
                        section.getExperimentalData(currentPicket.get())
                )
        );
    }

    private void updateModelTable() {
        ObservableList<ModelTableLine> modelTableLines = FXCollections.emptyObservableList();

        if (section.getModelData(currentPicket.get()) != null) {
            modelTableLines = VESTablesConverters.toModelTableData(
                    section.getModelData(currentPicket.get())
            );
        }

        modelTableData.setValue(modelTableLines);
    }

    public String getVesTitle() {
        return vesTitle.get();
    }

    public StringProperty vesTitleProperty() {
        return vesTitle;
    }

    public ObservableList<ExperimentalTableLine> getExpTableData() {
        return expTableData.get();
    }

    public ObjectProperty<ObservableList<ExperimentalTableLine>> expTableDataProperty() {
        return expTableData;
    }

    public ObservableList<ModelTableLine> getModelTableData() {
        return modelTableData.get();
    }

    public ObjectProperty<ObservableList<ModelTableLine>> modelTableDataProperty() {
        return modelTableData;
    }

    public boolean getNoFileOpened() {
        return noFileOpened.get();
    }

    public BooleanProperty noFileOpenedProperty() {
        return noFileOpened;
    }

    public int getCurrentPicket() {
        return currentPicket.get();
    }

    public IntegerProperty currentPicketProperty() {
        return currentPicket;
    }

    public String getVesNumber() {
        return vesNumber.get();
    }

    public StringProperty vesNumberProperty() {
        return vesNumber;
    }
}
