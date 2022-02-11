package ru.nucodelabs.gem.view.main;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.MODFile;
import ru.nucodelabs.files.sonet.STTFile;
import ru.nucodelabs.files.sonet.SonetImport;
import ru.nucodelabs.gem.core.ViewManager;
import ru.nucodelabs.gem.model.ConfigModel;
import ru.nucodelabs.gem.model.VESDataModel;
import ru.nucodelabs.gem.view.MisfitStacksSeriesConverters;
import ru.nucodelabs.gem.view.ModelCurveDragger;
import ru.nucodelabs.gem.view.VESSeriesConverters;
import ru.nucodelabs.mvvm.ViewModel;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class MainViewModel extends ViewModel {

    /**
     * <h3>Constants</h3>
     */
    private static final int EXP_CURVE_SERIES_CNT = 3;
    private static final int THEOR_CURVE_SERIES_CNT = 4;
    private static final int MOD_CURVE_SERIES_CNT = 5;
    private static final int EXP_CURVE_SERIES_INDEX = 0;
    private static final int EXP_CURVE_ERROR_UPPER_SERIES_INDEX = 1;
    private static final int EXP_CURVE_ERROR_LOWER_SERIES_INDEX = 2;
    private static final int THEOR_CURVE_SERIES_INDEX = THEOR_CURVE_SERIES_CNT - 1;
    private static final int MOD_CURVE_SERIES_INDEX = MOD_CURVE_SERIES_CNT - 1;

    private ModelCurveDragger modelCurveDragger;


    /**
     * <h3>Properties</h3>
     */
    private final ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> vesCurvesData;
    private final StringProperty vesText;
    private final ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> misfitStacksData;
    private final BooleanProperty menuFileMODDisabled;

    /**
     * Data models
     */
    private final VESDataModel vesData;
    private final ConfigModel config;

    /**
     * <h3>Constructor</h3>
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

        menuFileMODDisabled = new SimpleBooleanProperty(true);

        vesCurvesData = new SimpleObjectProperty<>(FXCollections.observableList(new ArrayList<>()));
        vesText = new SimpleStringProperty("");

        misfitStacksData = new SimpleObjectProperty<>(FXCollections.observableList(new ArrayList<>()));
    }

    /**
     * Initializes model dragger
     *
     * @param vesCurvesLineChart Line Chart
     */
    public void initModelCurveDragger(LineChart<Double, Double> vesCurvesLineChart) {
        modelCurveDragger = new ModelCurveDragger(vesCurvesLineChart, vesCurvesData, MOD_CURVE_SERIES_INDEX);
    }

    /**
     * Asks about import option then either addToCurrent() or addToNew() is called from ImportOptionPrompt
     */
    public void importEXP() {
        viewManager.askImportOption(this);
    }

    /**
     * Asks which file and then imports it to current window
     */
    public void addToCurrent() {
        addToCurrent(viewManager.showEXPFileChooser(this));
    }

    /**
     * Imports file to current window, then imports MOD
     *
     * @param file file to import
     */
    public void addToCurrent(File file) {
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

        addToModel(openedEXP, openedSTT);
        compatibilityModeAlert();

        menuFileMODDisabled.setValue(false);
        addEXPFileNameToVESText(file);
        updateExpCurves();

        if (misfitStacksData.getValue() != null) {
            misfitStacksData.getValue().clear();
        }
        importMOD();
    }

    /**
     * Adds EXP file name to vesText
     *
     * @param file EXP File
     */
    private void addEXPFileNameToVESText(File file) {
        vesText.setValue(file.getName());
    }

    /**
     * Warns about compatibility mode if data is unsafe
     */
    private void compatibilityModeAlert() {
        if (vesData.getPicket(0).getExperimentalData().isUnsafe()) {
            viewManager.alertExperimentalDataIsUnsafe(this);
        }
    }

    /**
     * Adds new picket defined by files to section
     *
     * @param openedEXP EXP
     * @param openedSTT STT
     */
    private void addToModel(EXPFile openedEXP, STTFile openedSTT) {
        if (vesData.getPickets().size() == 0) {
            vesData.addPicket(new Picket(openedEXP, openedSTT));
        } else {
            vesData.getPickets().set(0, new Picket(openedEXP, openedSTT));
        }
    }

    /**
     * Opens new window, in which asks which file and then imports it
     */
    public void addToNew() {
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

        addToModel(openedMOD);

        try {
            updateTheoreticalCurve();
        } catch (UnsatisfiedLinkError e) {
            viewManager.alertNoLib(this, e);
            return;
        }

        updateModelCurve();
        addMODFileNameToVESText(file);

        try {
            updateMisfitStacks();
        } catch (UnsatisfiedLinkError e) {
            viewManager.alertNoLib(this, e);
        }

        try {
            modelCurveDragger.initModelData(vesData.getModelData(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds MOD File name to vesText
     *
     * @param file MOD File
     */
    private void addMODFileNameToVESText(File file) {
        vesText.setValue(
                String.format(
                        "%s - %s",
                        vesText.getValue().split("\s")[0],
                        file.getName()
                )
        );
    }

    /**
     * Adds ModelData to picket
     */
    private void addToModel(MODFile openedMOD) {
        vesData.setModelData(0, new ModelData(openedMOD));
    }

    private void updateTheoreticalCurve() {
        XYChart.Series<Double, Double> theorCurveSeries = VESSeriesConverters.toTheoreticalCurveSeries(
                vesData.getExperimentalData(0), vesData.getModelData(0)
        );
        if (vesCurvesData.get().size() < MOD_CURVE_SERIES_CNT) {
            vesCurvesData.setValue(
                    FXCollections.observableList(
                            vesCurvesData.getValue().subList(0, EXP_CURVE_SERIES_CNT)
                    )
            );
            vesCurvesData.getValue().add(theorCurveSeries);
        } else if (vesCurvesData.get().size() == MOD_CURVE_SERIES_CNT) {
            vesCurvesData.get().set(THEOR_CURVE_SERIES_INDEX, theorCurveSeries);
        }

    }

    private void updateModelCurve() {
        XYChart.Series<Double, Double> modelCurveSeries = VESSeriesConverters.toModelCurveSeries(
                vesData.getModelData(0)
        );
        vesCurvesData.getValue().add(modelCurveSeries);
        modelCurveSeries.getNode().setCursor(Cursor.HAND);
        modelCurveSeries.getNode().setOnMousePressed(e -> modelCurveDragger.lineToDragDetector(e));
        modelCurveSeries.getNode().setOnMouseDragged(e -> {
            modelCurveDragger.dragHandler(e);
            updateMisfitStacks();
            updateTheoreticalCurve();
        });
    }

    private void updateExpCurves() {
        List<XYChart.Series<Double, Double>> expCurveSeries = VESSeriesConverters.toExperimentalCurveSeriesAll(
                vesData.getExperimentalData(0)
        );
        ArrayList<XYChart.Series<Double, Double>> seriesList = new ArrayList<>(expCurveSeries);
        vesCurvesData.setValue(
                FXCollections.observableList(new ArrayList<>())
        );
        vesCurvesData.getValue().setAll(seriesList);
    }

    private void updateMisfitStacks() {
        List<XYChart.Series<Double, Double>> misfitStacksSeriesList = MisfitStacksSeriesConverters.toMisfitStacksSeriesList(
                vesData.getExperimentalData(0), vesData.getModelData(0)
        );
        misfitStacksData.setValue(
                FXCollections.observableList(new ArrayList<>())
        );
        misfitStacksData.getValue().addAll(misfitStacksSeriesList);
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
}
