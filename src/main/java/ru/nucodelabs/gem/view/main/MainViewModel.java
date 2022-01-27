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
    protected static final int EXP_CURVE_SERIES_CNT = 3;
    protected static final int THEOR_CURVE_SERIES_CNT = 4;
    protected static final int MOD_CURVE_SERIES_CNT = 5;
    protected static final int EXP_CURVE_SERIES_INDEX = 0;
    protected static final int EXP_CURVE_ERROR_UPPER_SERIES_INDEX = 1;
    protected static final int EXP_CURVE_ERROR_LOWER_SERIES_INDEX = 2;
    protected static final int THEOR_CURVE_SERIES_INDEX = THEOR_CURVE_SERIES_CNT - 1;
    protected static final int MOD_CURVE_SERIES_INDEX = MOD_CURVE_SERIES_CNT - 1;

    /**
     * <h3>Properties</h3>
     */
    private final ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> vesCurvesData;
    private final BooleanProperty vesCurvesVisible;
    private final StringProperty vesText;
    private final ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> misfitStacksData;
    private final BooleanProperty misfitStacksVisible;
    private final BooleanProperty menuFileMODDisabled;
    private ModelCurveDragger modelCurveDragger;

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

        vesCurvesVisible = new SimpleBooleanProperty(false);
        vesCurvesData = new SimpleObjectProperty<>();
        vesText = new SimpleStringProperty("");

        misfitStacksVisible = new SimpleBooleanProperty(false);
        misfitStacksData = new SimpleObjectProperty<>();
    }

    public void initModelCurveDragger(LineChart<Double, Double> vesCurvesLineChart) {
        modelCurveDragger = new ModelCurveDragger(vesCurvesLineChart);
    }

    public void importEXP() {
        importEXP(viewManager.showEXPFileChooser());
    }

    public void importEXP(File file) {
        if (file == null) {
            return;
        }

        Path openedFilePath = file.toPath();

        EXPFile openedEXP;
        try {
            openedEXP = SonetImport.readEXP(file);
        } catch (Exception e) {
            viewManager.alertIncorrectFile(e);
            return;
        }

        STTFile openedSTT;
        try {
            openedSTT = SonetImport.readSTT(new File(
                    openedFilePath.getParent().toString()
                            + File.separator
                            + openedEXP.getSTTFileName()));
        } catch (Exception e) {
            viewManager.alertIncorrectFile(e);
            return;
        }
        if (vesData.getPickets().size() == 0) {
            vesData.addPicket(new Picket(openedEXP, openedSTT));
        } else {
            vesData.getPickets().set(0, new Picket(openedEXP, openedSTT));
        }
        if (vesData.getPicket(vesData.getPickets().size() - 1).getExperimentalData().isUnsafe()) {
            viewManager.alertExperimentalDataIsUnsafe();
        }

        menuFileMODDisabled.setValue(false);
        vesText.setValue(file.getName());
        updateExpCurveData();

        misfitStacksVisible.setValue(false);
        vesCurvesVisible.setValue(true);
    }

    public void importMOD() {
        importMOD(viewManager.showMODFileChooser());
    }

    public void importMOD(File file) {

        if (file == null) {
            return;
        }

        MODFile openedMOD;
        try {
            openedMOD = SonetImport.readMOD(file);
        } catch (Exception e) {
            viewManager.alertIncorrectFile(e);
            return;
        }

        vesData.setModelData(0, new ModelData(openedMOD));

        try {
            updateTheoreticalCurve();
        } catch (UnsatisfiedLinkError e) {
            viewManager.alertNoLib(e);
            return;
        }

        updateModelCurve();
        vesText.setValue(vesText.getValue() + " - " + file.getName());
        try {
            updateMisfitStacksData();
        } catch (UnsatisfiedLinkError e) {
            viewManager.alertNoLib(e);
            return;
        }
        misfitStacksVisible.setValue(true);
    }

    private void updateTheoreticalCurve() {
        XYChart.Series<Double, Double> theorCurveSeries = VESSeriesConverters.toTheoreticalCurveSeries(
                vesData.getExperimentalData(0), vesData.getModelData(0)
        );
        vesCurvesData.setValue(
                FXCollections.observableList(
                        vesCurvesData.getValue().subList(0, EXP_CURVE_SERIES_CNT)
                )
        );
        vesCurvesData.getValue().add(theorCurveSeries);
    }

    private void updateModelCurve() {
        XYChart.Series<Double, Double> modelCurveSeries = VESSeriesConverters.toModelCurveSeries(
                vesData.getModelData(0)
        );
        vesCurvesData.getValue().add(modelCurveSeries);
        modelCurveSeries.getNode().setCursor(Cursor.HAND);
        modelCurveSeries.getNode().setOnMousePressed(e -> modelCurveDragger.lineToDragDetector(e));
        modelCurveSeries.getNode().setOnMouseDragged(e -> modelCurveDragger.dragHandler(e));
    }

    private void updateExpCurveData() {
        List<XYChart.Series<Double, Double>> expCurveSeries = VESSeriesConverters.toExperimentalCurveSeriesAll(
                vesData.getExperimentalData(0)
        );
        ArrayList<XYChart.Series<Double, Double>> seriesList = new ArrayList<>(expCurveSeries);
        vesCurvesData.setValue(
                FXCollections.observableList(new ArrayList<>())
        );
        vesCurvesData.getValue().setAll(seriesList);
    }

    private void updateMisfitStacksData() {
        List<XYChart.Series<Double, Double>> misfitStacksSeriesList = MisfitStacksSeriesConverters.toMisfitStacksSeriesList(
                vesData.getExperimentalData(0), vesData.getModelData(0)
        );
        misfitStacksData.setValue(
                FXCollections.observableList(new ArrayList<>())
        );
        misfitStacksData.getValue().addAll(misfitStacksSeriesList);
        colorizeMisfitStacksSeries(misfitStacksData);
    }

    private void colorizeMisfitStacksSeries(ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> misfitStacksData) {
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

    public boolean getVesCurvesVisible() {
        return vesCurvesVisible.get();
    }

    public BooleanProperty vesCurvesVisibleProperty() {
        return vesCurvesVisible;
    }

    public boolean getMisfitStacksVisible() {
        return misfitStacksVisible.get();
    }

    public BooleanProperty misfitStacksVisibleProperty() {
        return misfitStacksVisible;
    }

    public String getVesText() {
        return vesText.get();
    }

    public StringProperty vesTextProperty() {
        return vesText;
    }
}
