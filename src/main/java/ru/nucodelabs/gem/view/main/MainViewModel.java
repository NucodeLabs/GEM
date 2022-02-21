package ru.nucodelabs.gem.view.main;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.chart.XYChart;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.files.gem.GemJson;
import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.MODFile;
import ru.nucodelabs.files.sonet.STTFile;
import ru.nucodelabs.files.sonet.SonetImport;
import ru.nucodelabs.gem.core.FileService;
import ru.nucodelabs.gem.core.ViewManager;
import ru.nucodelabs.gem.model.ConfigModel;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.view.*;
import ru.nucodelabs.gem.view.usercontrols.vestables.tablelines.ExperimentalTableLine;
import ru.nucodelabs.gem.view.usercontrols.vestables.tablelines.ModelTableLine;
import ru.nucodelabs.mvvm.ViewModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.lang.Math.abs;

public class MainViewModel extends ViewModel {

    /**
     * Constants
     */
    private static final int EXP_CURVE_SERIES_CNT = 3;
    private static final int THEOR_CURVE_SERIES_CNT = 4;
    private static final int MOD_CURVE_SERIES_CNT = 5;
    private static final int EXP_CURVE_SERIES_INDEX = 0;
    private static final int EXP_CURVE_ERROR_UPPER_SERIES_INDEX = 1;
    private static final int EXP_CURVE_ERROR_LOWER_SERIES_INDEX = 2;
    private static final int THEOR_CURVE_SERIES_INDEX = THEOR_CURVE_SERIES_CNT - 1;
    private static final int MOD_CURVE_SERIES_INDEX = MOD_CURVE_SERIES_CNT - 1;

    /**
     * Service-objects
     */
    private ModelCurveDragger modelCurveDragger;
    private final VESCurvesNavigator vesCurvesNavigator;
    private final FileService fileService;

    /**
     * Properties
     */
    private final ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> vesCurvesData;
    private final StringProperty vesTitle;
    private final StringProperty vesNumber;
    private final ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> misfitStacksData;
    private final BooleanProperty menuFileMODDisabled;
    private final DoubleProperty vesCurvesXLowerBound;
    private final DoubleProperty vesCurvesXUpperBound;
    private final DoubleProperty vesCurvesYLowerBound;
    private final DoubleProperty vesCurvesYUpperBound;
    private final ObjectProperty<ObservableList<ExperimentalTableLine>> expTableData;
    private final ObjectProperty<ObservableList<ModelTableLine>> modelTableData;
    private final BooleanProperty noFileOpened;
    private final IntegerProperty currentPicket;

    /**
     * Data models
     */
    private Section section;
    private final ConfigModel config;

    /**
     * Initialization
     *
     * @param viewManager View Manager
     * @param configModel Configuration
     * @param section     VES Data
     */
    public MainViewModel(ViewManager viewManager, ConfigModel configModel, Section section) {
        super(viewManager);
        this.config = configModel;
        this.section = section;

        fileService = new FileService();
        currentPicket = new SimpleIntegerProperty(-1);

        vesCurvesXLowerBound = new SimpleDoubleProperty(-1);
        vesCurvesXUpperBound = new SimpleDoubleProperty(4);
        vesCurvesYLowerBound = new SimpleDoubleProperty(-1);
        vesCurvesYUpperBound = new SimpleDoubleProperty(4);
        vesCurvesNavigator = new VESCurvesNavigator(
                vesCurvesXLowerBound, vesCurvesXUpperBound,
                vesCurvesYLowerBound, vesCurvesYUpperBound,
                0.1
        );

        menuFileMODDisabled = new SimpleBooleanProperty(true);
        noFileOpened = new SimpleBooleanProperty(true);

        vesCurvesData = new SimpleObjectProperty<>(FXCollections.observableList(new ArrayList<>()));
        for (int i = 0; i < MOD_CURVE_SERIES_CNT; i++) {
            vesCurvesData.get().add(new XYChart.Series<>());
        }

        vesTitle = new SimpleStringProperty("");

        misfitStacksData = new SimpleObjectProperty<>(FXCollections.observableList(new ArrayList<>()));

        expTableData = new SimpleObjectProperty<>(FXCollections.observableList(new ArrayList<>()));
        modelTableData = new SimpleObjectProperty<>(FXCollections.observableList(new ArrayList<>()));
        vesNumber = new SimpleStringProperty("0/0");
    }

    /**
     * Initializes model dragger
     *
     * @param coordinateInSceneToValue line chart node dependent function to convert X and Y in values for axis
     */
    public void initModelCurveDragger(Function<Point2D, XYChart.Data<Double, Double>> coordinateInSceneToValue) {
        modelCurveDragger = new ModelCurveDragger(coordinateInSceneToValue, vesCurvesData, MOD_CURVE_SERIES_INDEX);
    }

    /**
     * Asks which EXP files and then imports them to current window
     */
    public void importEXP() {
        List<File> files = viewManager.showOpenEXPFileChooser(this);
        if (files != null && files.size() != 0) {
            for (var file : files) {
                importEXP(file);
            }
        }
    }

    private void importEXP(File file) {
        EXPFile openedEXP;
        try {
            openedEXP = SonetImport.readEXP(file);
        } catch (Exception e) {
            viewManager.alertIncorrectFile(this, e);
            return;
        }

        Path openedFilePath = file.toPath();

        STTFile openedSTT;
        try {
            openedSTT = SonetImport.readSTT(new File(
                    openedFilePath.getParent().toString()
                            + File.separator
                            + openedEXP.getSTTFileName()));
        } catch (Exception e) {
            viewManager.alertIncorrectFile(this, e);
            return;
        }

        Picket addedPicket = addToVESDataModel(openedEXP, openedSTT);
        fileService.addAssociation(addedPicket.getExperimentalData(), file);
        currentPicket.set(currentPicket.get() + 1);
        compatibilityModeAlert();

        updateAll();
    }

    public void openSection() {
        File file = viewManager.showOpenJsonFileChooser(this);
        if (file != null) {
            try {
                section = GemJson.readSection(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            currentPicket.set(0);
            updateAll();
        }
    }

    public void saveSection() {
        File file = viewManager.showSaveJsonFileChooser(this);
        if (file != null) {
            try {
                GemJson.writeData(section, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds files names to vesText
     */
    private void updateVESText() {
        File expDataFile = fileService.getAssociatedFile(section.getExperimentalData(currentPicket.get()));
        File modDataFile = fileService.getAssociatedFile(section.getModelData(currentPicket.get()));
        if (expDataFile != null && modDataFile != null) {
            vesTitle.set(expDataFile.getName() + " - " + modDataFile.getName());
        } else if (expDataFile != null) {
            vesTitle.set(expDataFile.getName());
        } else if (modDataFile != null) {
            vesTitle.set(section.getName(currentPicket.get()) + " - " + modDataFile.getName());
        } else {
            vesTitle.set(section.getName(currentPicket.get()));
        }
    }

    private void updateVESNumber() {
        vesNumber.set(currentPicket.get() + 1 + "/" + section.getPicketsCount());
    }

    /**
     * Warns about compatibility mode if data is unsafe
     */
    private void compatibilityModeAlert() {
        ExperimentalData experimentalData = section.getPicket(currentPicket.get()).getExperimentalData();
        if (experimentalData.isUnsafe()) {
            viewManager.alertExperimentalDataIsUnsafe(this, fileService.getAssociatedFile(experimentalData).getName());
        }
    }

    /**
     * Adds new picket defined by files to section
     *
     * @param openedEXP EXP
     * @param openedSTT STT
     * @return added picket
     */
    private Picket addToVESDataModel(EXPFile openedEXP, STTFile openedSTT) {
        section.addPicket(new Picket(openedEXP, openedSTT));
        return section.getLastPicket();
    }

    /**
     * Opens new window
     */
    public void newWindow() {
        viewManager.start();
    }

    /**
     * Asks which file to import and then import it
     */
    public void importMOD() {
        File file = viewManager.showOpenMODFileChooser(this);

        if (file == null) {
            return;
        }

        MODFile openedMOD;
        try {
            openedMOD = SonetImport.readMOD(file);
        } catch (Exception e) {
            viewManager.alertIncorrectFile(this, e);
            return;
        }

        Picket picketWithModelData = addToVESDataModel(openedMOD);
        fileService.addAssociation(picketWithModelData.getModelData(), file);
        updateAll();
    }

    /**
     * Adds ModelData to picket
     *
     * @return picket which model data assigned to
     */
    private Picket addToVESDataModel(MODFile openedMOD) {
        section.setModelData(currentPicket.get(), new ModelData(openedMOD));
        return section.getPicket(currentPicket.get());
    }

    private void updateAll() {
        if (section.getPicketsCount() > 0) {
            noFileOpened.set(false);
            menuFileMODDisabled.set(false);
        }
        updateExpTable();
        updateExpCurves();
        updateTheoreticalCurve();
        updateModelCurve();
        updateModelTable();
        updateMisfitStacks();
        updateVESText();
        updateVESNumber();
    }

    public void switchToNextPicket() {
        if (section.getPicketsCount() > currentPicket.get() + 1) {
            currentPicket.set(currentPicket.get() + 1);
            updateAll();
        }
    }

    public void switchToPrevPicket() {
        if (currentPicket.get() > 0 && section.getPicketsCount() > 0) {
            currentPicket.set(currentPicket.get() - 1);
            updateAll();
        }
    }

    public void zoomInVesCurves() {
        vesCurvesNavigator.zoomIn();
    }

    public void zoomOutVesCurves() {
        vesCurvesNavigator.zoomOut();
    }

    public void moveLeftVesCurves() {
        vesCurvesNavigator.moveLeft();
    }

    public void moveRightVesCurves() {
        vesCurvesNavigator.moveRight();
    }

    public void moveUpVesCurves() {
        vesCurvesNavigator.moveUp();
    }

    public void moveDownVesCurves() {
        vesCurvesNavigator.moveDown();
    }

    private void updateTheoreticalCurve() {
        XYChart.Series<Double, Double> theorCurveSeries = new XYChart.Series<>();

        if (section.getModelData(currentPicket.get()) != null) {
            try {
                theorCurveSeries = VESSeriesConverters.toTheoreticalCurveSeries(
                        section.getExperimentalData(currentPicket.get()), section.getModelData(currentPicket.get())
                );
            } catch (UnsatisfiedLinkError e) {
                viewManager.alertNoLib(this, e);
            }
        }

        vesCurvesData.get().set(THEOR_CURVE_SERIES_INDEX, theorCurveSeries);
    }

    private void updateModelCurve() {
        XYChart.Series<Double, Double> modelCurveSeries = new XYChart.Series<>();

        if (section.getModelData(currentPicket.get()) != null) {
            modelCurveSeries = VESSeriesConverters.toModelCurveSeries(
                    section.getModelData(currentPicket.get())
            );
            vesCurvesData.get().set(MOD_CURVE_SERIES_INDEX, modelCurveSeries);
            addDraggingToModelCurveSeries(modelCurveSeries);
        } else {
            vesCurvesData.get().set(MOD_CURVE_SERIES_INDEX, modelCurveSeries);
        }
    }

    private void addDraggingToModelCurveSeries(XYChart.Series<Double, Double> modelCurveSeries) {
        try {
            modelCurveDragger.initModelData(section.getModelData(currentPicket.get()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        modelCurveSeries.getNode().setCursor(Cursor.HAND);
        modelCurveSeries.getNode().setOnMousePressed(e -> modelCurveDragger.lineToDragDetector(e));
        modelCurveSeries.getNode().setOnMouseDragged(e -> {
            modelCurveDragger.dragHandler(e);
            updateMisfitStacks();
            updateTheoreticalCurve();
            updateModelTable();
        });
    }

    private void updateExpCurves() {
        XYChart.Series<Double, Double> expCurveSeries = VESSeriesConverters.toExperimentalCurveSeries(
                section.getExperimentalData(currentPicket.get())
        );
        XYChart.Series<Double, Double> errUpperExp = VESSeriesConverters.toErrorExperimentalCurveUpperBoundSeries(
                section.getExperimentalData(currentPicket.get())
        );
        XYChart.Series<Double, Double> errLowerExp = VESSeriesConverters.toErrorExperimentalCurveLowerBoundSeries(
                section.getExperimentalData(currentPicket.get())
        );
        vesCurvesData.get().set(EXP_CURVE_SERIES_INDEX, expCurveSeries);
        vesCurvesData.get().set(EXP_CURVE_ERROR_UPPER_SERIES_INDEX, errUpperExp);
        vesCurvesData.get().set(EXP_CURVE_ERROR_LOWER_SERIES_INDEX, errLowerExp);
        if (section.getModelData(currentPicket.get()) == null) {
            vesCurvesData.get().set(THEOR_CURVE_SERIES_INDEX, new XYChart.Series<>());
            vesCurvesData.get().set(MOD_CURVE_SERIES_INDEX, new XYChart.Series<>());
        }
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

    private void updateMisfitStacks() {
        List<XYChart.Series<Double, Double>> misfitStacksSeriesList = new ArrayList<>();

        if (section.getModelData(currentPicket.get()) != null) {
            try {
                misfitStacksSeriesList = MisfitStacksSeriesConverters.toMisfitStacksSeriesList(
                        section.getExperimentalData(currentPicket.get()), section.getModelData(currentPicket.get())
                );
            } catch (UnsatisfiedLinkError e) {
                viewManager.alertNoLib(this, e);
            }
        }

        misfitStacksData.set(FXCollections.observableList(new ArrayList<>()));
        misfitStacksData.get().addAll(misfitStacksSeriesList);
        colorizeMisfitStacksSeries();
    }

    /**
     * Colorizes misfit stacks with green and red, green for ones that <100%, red for ≥100%
     */
    private void colorizeMisfitStacksSeries() {
        var data = misfitStacksData.get();
        for (var series : data) {
            var nonZeroPoint = series.getData().get(1);
            if (abs(nonZeroPoint.getYValue()) < 100f) {
                series.getNode().setStyle("-fx-stroke: LimeGreen;");
                nonZeroPoint.getNode().lookup(".chart-line-symbol").setStyle("-fx-background-color: LimeGreen");
                var zeroPoint = series.getData().get(0);
                zeroPoint.getNode().lookup(".chart-line-symbol").setStyle("-fx-background-color: LimeGreen");
            }
        }
    }

    public ObservableList<XYChart.Series<Double, Double>> getVesCurvesData() {
        return vesCurvesData.get();
    }

    public ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> vesCurvesDataProperty() {
        return vesCurvesData;
    }

    public ObservableList<XYChart.Series<Double, Double>> getMisfitStacksData() {
        return misfitStacksData.get();
    }

    public ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> misfitStacksDataProperty() {
        return misfitStacksData;
    }

    public boolean isMenuFileMODDisabled() {
        return menuFileMODDisabled.get();
    }

    public BooleanProperty menuFileMODDisabledProperty() {
        return menuFileMODDisabled;
    }

    public String getVesTitle() {
        return vesTitle.get();
    }

    public StringProperty vesTitleProperty() {
        return vesTitle;
    }

    public double getVesCurvesXLowerBound() {
        return vesCurvesXLowerBound.get();
    }

    public DoubleProperty vesCurvesXLowerBoundProperty() {
        return vesCurvesXLowerBound;
    }

    public double getVesCurvesXUpperBound() {
        return vesCurvesXUpperBound.get();
    }

    public DoubleProperty vesCurvesXUpperBoundProperty() {
        return vesCurvesXUpperBound;
    }

    public double getVesCurvesYLowerBound() {
        return vesCurvesYLowerBound.get();
    }

    public DoubleProperty vesCurvesYLowerBoundProperty() {
        return vesCurvesYLowerBound;
    }

    public double getVesCurvesYUpperBound() {
        return vesCurvesYUpperBound.get();
    }

    public DoubleProperty vesCurvesYUpperBoundProperty() {
        return vesCurvesYUpperBound;
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
