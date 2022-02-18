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
import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.MODFile;
import ru.nucodelabs.files.sonet.STTFile;
import ru.nucodelabs.files.sonet.SonetImport;
import ru.nucodelabs.gem.core.ViewManager;
import ru.nucodelabs.gem.model.ConfigModel;
import ru.nucodelabs.gem.model.VESDataModel;
import ru.nucodelabs.gem.view.*;
import ru.nucodelabs.gem.view.usercontrols.vestables.tablelines.ExperimentalTableLine;
import ru.nucodelabs.gem.view.usercontrols.vestables.tablelines.ModelTableLine;
import ru.nucodelabs.mvvm.ViewModel;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final Map<ExperimentalData, File> experimentalDataFileMap;
    private final Map<ModelData, File> modelDataFileMap;
    //TODO: Сделать некоторый объект-сервис для файлов, ведь дальше надо будет проверять сохранены измнения или нет и тп


    /**
     * Properties
     */
    private final ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> vesCurvesData;
    private final StringProperty vesText;
    private final ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> misfitStacksData;
    private final BooleanProperty menuFileMODDisabled;
    private final DoubleProperty vesCurvesXLowerBound;
    private final DoubleProperty vesCurvesXUpperBound;
    private final DoubleProperty vesCurvesYLowerBound;
    private final DoubleProperty vesCurvesYUpperBound;
    private final ObjectProperty<ObservableList<ExperimentalTableLine>> expTableData;
    private final ObjectProperty<ObservableList<ModelTableLine>> modelTableData;
    private final BooleanProperty welcomeScreenVisible;
    private final IntegerProperty currentPicket;

    /**
     * Data models
     */
    private final VESDataModel vesData;
    private final ConfigModel config;

    /**
     * Initialization
     *
     * @param viewManager  View Manager
     * @param configModel  Configuration
     * @param vesDataModel VES Data
     */
    public MainViewModel(ViewManager viewManager, ConfigModel configModel, VESDataModel vesDataModel) {
        super(viewManager);
        this.config = configModel;
        this.vesData = vesDataModel;

        experimentalDataFileMap = new HashMap<>();
        modelDataFileMap = new HashMap<>();
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
        welcomeScreenVisible = new SimpleBooleanProperty(true);

        vesCurvesData = new SimpleObjectProperty<>(FXCollections.observableList(new ArrayList<>()));
        for (int i = 0; i < MOD_CURVE_SERIES_CNT; i++) {
            vesCurvesData.get().add(new XYChart.Series<>());
        }

        vesText = new SimpleStringProperty("");

        misfitStacksData = new SimpleObjectProperty<>(FXCollections.observableList(new ArrayList<>()));

        expTableData = new SimpleObjectProperty<>(FXCollections.observableList(new ArrayList<>()));
        modelTableData = new SimpleObjectProperty<>(FXCollections.observableList(new ArrayList<>()));
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
     * Asks about import option then either addToCurrent() or addToNew() is called from ImportOptionPrompt
     */
    public void importEXP() {
        if (vesData.getPicketsCount() > 0) {
            viewManager.askImportOption(this);
        } else {
            addEXPToCurrent(viewManager.showEXPFileChooser(this));
        }
    }

    /**
     * Asks which file and then imports it to current window
     */
    public void addEXPToCurrent() {
        addEXPToCurrent(viewManager.showEXPFileChooser(this));
    }

    /**
     * Imports file to current window
     *
     * @param file file to import
     */
    public void addEXPToCurrent(File file) {
        if (file == null) {
            return;
        }

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
        experimentalDataFileMap.put(addedPicket.getExperimentalData(), file);
        currentPicket.set(currentPicket.get() + 1);
        compatibilityModeAlert();

        menuFileMODDisabled.setValue(false);
        updateVESText();
        updateExpCurves();
        updateExpTable();

        if (misfitStacksData.getValue() != null) {
            misfitStacksData.getValue().clear();
        }

        welcomeScreenVisible.set(false);
    }

    /**
     * Adds files names to vesText
     */
    private void updateVESText() {
        File expDataFile = experimentalDataFileMap.get(vesData.getExperimentalData(currentPicket.get()));
        File modDataFile = modelDataFileMap.get(vesData.getModelData(currentPicket.get()));
        if (expDataFile != null && modDataFile != null) {
            vesText.set(expDataFile.getName() + " - " + modDataFile.getName());
        } else if (expDataFile != null) {
            vesText.set(expDataFile.getName());
        }
    }

    /**
     * Warns about compatibility mode if data is unsafe
     */
    private void compatibilityModeAlert() {
        if (vesData.getPicket(currentPicket.get()).getExperimentalData().isUnsafe()) {
            viewManager.alertExperimentalDataIsUnsafe(this);
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
        vesData.addPicket(new Picket(openedEXP, openedSTT));
        return vesData.getLastPicket();
    }

    /**
     * Opens new window, in which asks which file and then imports it
     */
    public void addEXPToNew() {
        viewManager.newMainViewWithImportEXP(this);
    }

    /**
     * Asks which file to import and then import it
     */
    public void importMOD() {
        File file = viewManager.showMODFileChooser(this);

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
        modelDataFileMap.put(picketWithModelData.getModelData(), file);

        try {
            updateTheoreticalCurve();
        } catch (UnsatisfiedLinkError e) {
            viewManager.alertNoLib(this, e);
            return;
        }

        updateModelCurve();
        updateModelTable();
        updateVESText();

        try {
            updateMisfitStacks();
        } catch (UnsatisfiedLinkError e) {
            viewManager.alertNoLib(this, e);
        }
    }

    /**
     * Adds ModelData to picket
     *
     * @return picket which model data assigned to
     */
    private Picket addToVESDataModel(MODFile openedMOD) {
        vesData.setModelData(currentPicket.get(), new ModelData(openedMOD));
        return vesData.getPicket(currentPicket.get());
    }

    private void updateAll() {
        updateExpTable();
        updateModelTable();
        updateExpCurves();
        updateTheoreticalCurve();
        updateModelCurve();
        updateMisfitStacks();
        updateVESText();
    }

    public void switchToNextPicket() {
        if (vesData.getPicketsCount() > currentPicket.get() + 1) {
            currentPicket.set(currentPicket.get() + 1);
            updateAll();
        }
    }

    public void switchToPrevPicket() {
        if (currentPicket.get() > 0 && vesData.getPicketsCount() > 0) {
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

        if (vesData.getModelData(currentPicket.get()) != null) {
            theorCurveSeries = VESSeriesConverters.toTheoreticalCurveSeries(
                    vesData.getExperimentalData(currentPicket.get()), vesData.getModelData(currentPicket.get())
            );
        }

        vesCurvesData.get().set(THEOR_CURVE_SERIES_INDEX, theorCurveSeries);

    }

    private void updateModelCurve() {
        XYChart.Series<Double, Double> modelCurveSeries = new XYChart.Series<>();

        if (vesData.getModelData(currentPicket.get()) != null) {
            modelCurveSeries = VESSeriesConverters.toModelCurveSeries(
                    vesData.getModelData(currentPicket.get())
            );
            vesCurvesData.get().set(MOD_CURVE_SERIES_INDEX, modelCurveSeries);
            addDraggingToModelCurveSeries(modelCurveSeries);
        } else {
            vesCurvesData.get().set(MOD_CURVE_SERIES_INDEX, modelCurveSeries);
        }
    }

    private void addDraggingToModelCurveSeries(XYChart.Series<Double, Double> modelCurveSeries) {
        try {
            modelCurveDragger.initModelData(vesData.getModelData(currentPicket.get()));
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
                vesData.getExperimentalData(currentPicket.get())
        );
        XYChart.Series<Double, Double> errUpperExp = VESSeriesConverters.toErrorExperimentalCurveUpperBoundSeries(
                vesData.getExperimentalData(currentPicket.get())
        );
        XYChart.Series<Double, Double> errLowerExp = VESSeriesConverters.toErrorExperimentalCurveLowerBoundSeries(
                vesData.getExperimentalData(currentPicket.get())
        );
        vesCurvesData.get().set(EXP_CURVE_SERIES_INDEX, expCurveSeries);
        vesCurvesData.get().set(EXP_CURVE_ERROR_UPPER_SERIES_INDEX, errUpperExp);
        vesCurvesData.get().set(EXP_CURVE_ERROR_LOWER_SERIES_INDEX, errLowerExp);
        vesCurvesData.get().set(THEOR_CURVE_SERIES_INDEX, new XYChart.Series<>());
        vesCurvesData.get().set(MOD_CURVE_SERIES_INDEX, new XYChart.Series<>());
    }

    private void updateExpTable() {
        expTableData.setValue(
                VESTablesConverters.toExperimentalTableData(
                        vesData.getExperimentalData(currentPicket.get())
                )
        );
    }

    private void updateModelTable() {
        ObservableList<ModelTableLine> modelTableLines = FXCollections.emptyObservableList();

        if (vesData.getModelData(currentPicket.get()) != null) {
            modelTableLines = VESTablesConverters.toModelTableData(
                    vesData.getModelData(currentPicket.get())
            );
        }

        modelTableData.setValue(modelTableLines);
    }

    private void updateMisfitStacks() {
        List<XYChart.Series<Double, Double>> misfitStacksSeriesList = new ArrayList<>();

        if (vesData.getModelData(currentPicket.get()) != null) {
            misfitStacksSeriesList = MisfitStacksSeriesConverters.toMisfitStacksSeriesList(
                    vesData.getExperimentalData(currentPicket.get()), vesData.getModelData(currentPicket.get())
            );
        }

        misfitStacksData.set(FXCollections.observableList(new ArrayList<>()));
        misfitStacksData.get().addAll(misfitStacksSeriesList);
        colorizeMisfitStacksSeries();
    }

    /**
     * Colorizes misfit stacks with green and red, green for ones that <100%, red for ≥100%
     */
    private void colorizeMisfitStacksSeries() {
        misfitStacksData.getValue().forEach(
                s -> {
                    if (abs(s.getData().get(1).getYValue()) < 100f) {
                        s.getNode().setStyle("-fx-stroke: LimeGreen;");
                        s.getData().get(1).getNode().lookup(".chart-line-symbol").setStyle("-fx-background-color: LimeGreen");
                        s.getData().get(0).getNode().lookup(".chart-line-symbol").setStyle("-fx-background-color: LimeGreen");
                    }
                }
        );
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

    public String getVesText() {
        return vesText.get();
    }

    public StringProperty vesTextProperty() {
        return vesText;
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

    public boolean isWelcomeScreenVisible() {
        return welcomeScreenVisible.get();
    }

    public BooleanProperty welcomeScreenVisibleProperty() {
        return welcomeScreenVisible;
    }

    public int getCurrentPicket() {
        return currentPicket.get();
    }

    public IntegerProperty currentPicketProperty() {
        return currentPicket;
    }
}
