package ru.nucodelabs.gem.view.main;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.MODFile;
import ru.nucodelabs.files.sonet.STTFile;
import ru.nucodelabs.files.sonet.SonetImport;
import ru.nucodelabs.gem.core.ViewManager;
import ru.nucodelabs.gem.model.VESDataModel;
import ru.nucodelabs.gem.view.MisfitStacksSeriesConverters;
import ru.nucodelabs.gem.view.ModelCurveDragger;
import ru.nucodelabs.gem.view.VESSeriesConverters;
import ru.nucodelabs.mvvm.ViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class MainViewModel extends ViewModel<VESDataModel> {

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
    private final BooleanProperty vesLineChartVisible;
    private final BooleanProperty vesLegendsVisible;
    private final StringProperty vesText;
    private final ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> misfitStacksData;
    private final BooleanProperty misfitStacksLineChartVisible;
    private final BooleanProperty menuFileMODDisabled;
    private ModelCurveDragger modelCurveDragger;

    /**
     * <h3>Constructor</h3>
     * Initialization
     *
     * @param vesDataModel VES Data Model
     * @param viewManager  View Manager
     */
    public MainViewModel(VESDataModel vesDataModel, ViewManager viewManager) {
        super(vesDataModel, viewManager);

        menuFileMODDisabled = new SimpleBooleanProperty(true);

        vesLineChartVisible = new SimpleBooleanProperty(false);
        vesLegendsVisible = new SimpleBooleanProperty(true);
        vesCurvesData = new SimpleObjectProperty<>();
        vesText = new SimpleStringProperty("");

        misfitStacksLineChartVisible = new SimpleBooleanProperty(false);
        misfitStacksData = new SimpleObjectProperty<>();
    }

    protected void initModelCurveDragger(LineChart<Double, Double> vesCurvesLineChart) {
        modelCurveDragger = new ModelCurveDragger(vesCurvesLineChart);
    }

    private void alertExperimentalDataIsUnsafe() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Режим совместимости");
        alert.setHeaderText("STT и EXP содержат разное количество строк");
        alert.setContentText("Будет отображаться минимально возможное число данных");
        alert.initOwner(viewManager.getStage());
        alert.show();
    }

    private void alertFileNotFound(FileNotFoundException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Файл не найден!");
        alert.setContentText(e.getMessage());
        alert.initOwner(viewManager.getStage());
        alert.show();
    }

    private void alertNoLib(UnsatisfiedLinkError e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Невозможно отрисовать график");
        alert.setHeaderText("Отсутствует библиотека");
        alert.setContentText(e.getMessage());
        alert.initOwner(viewManager.getStage());
        alert.show();
    }

    public boolean importEXP() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите файл полевых данных для интерпретации");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("EXP - Полевые данные", "*.EXP", "*.exp")
        );
        File file = chooser.showOpenDialog(viewManager.getStage());
//      если закрыть окно выбора файла, ничего не выбрав, то FileChooser вернет null
        if (file == null) {
            return false;
        }

        Path openedFilePath = file.toPath();

        EXPFile openedEXP;
        try {
            openedEXP = SonetImport.readEXP(file);
        } catch (FileNotFoundException e) {
            alertFileNotFound(e);
            return false;
        }

        STTFile openedSTT;
        try {
            openedSTT = SonetImport.readSTT(new File(
                    openedFilePath.getParent().toString()
                            + File.separator
                            + openedEXP.getSTTFileName()));
        } catch (FileNotFoundException e) {
            alertFileNotFound(e);
            return false;
        }
        if (model.getPickets().size() == 0) {
            model.addPicket(new Picket(openedEXP, openedSTT));
        } else {
            model.getPickets().set(0, new Picket(openedEXP, openedSTT));
        }
        if (model.getPicket(model.getPickets().size() - 1).getExperimentalData().isUnsafe()) {
            alertExperimentalDataIsUnsafe();
        }

        menuFileMODDisabled.setValue(false);
        vesText.setValue(file.getName());
        updateExpCurveData();

        misfitStacksLineChartVisible.setValue(false);
        vesLineChartVisible.setValue(true);
        return true;
    }

    public boolean importMOD() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите файл модели");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MOD - Данные модели", "*.MOD", "*.mod")
        );
        File file = chooser.showOpenDialog(viewManager.getStage());
        if (file == null) {
            return false;
        }

        MODFile openedMOD;
        try {
            openedMOD = SonetImport.readMOD(file);
        } catch (FileNotFoundException e) {
            alertFileNotFound(e);
            return false;
        }

        model.setModelData(0, new ModelData(openedMOD));

        try {
            updateTheoreticalCurve();
        } catch (UnsatisfiedLinkError e) {
            alertNoLib(e);
            return false;
        }

        updateModelCurve();
        vesText.setValue(vesText.getValue() + " - " + file.getName());
        try {
            updateMisfitStacksData();
        } catch (UnsatisfiedLinkError e) {
            alertNoLib(e);
            return false;
        }
        misfitStacksLineChartVisible.setValue(true);
        return true;
    }

    private void updateTheoreticalCurve() {
        XYChart.Series<Double, Double> theorCurveSeries = VESSeriesConverters.toTheoreticalCurveSeries(
                model.getExperimentalData(0), model.getModelData(0)
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
                model.getModelData(0)
        );
        vesCurvesData.getValue().add(modelCurveSeries);
        modelCurveSeries.getNode().setCursor(Cursor.HAND);
        modelCurveSeries.getNode().setOnMousePressed(e -> modelCurveDragger.lineToDragDetector(e));
        modelCurveSeries.getNode().setOnMouseDragged(e -> modelCurveDragger.dragHandler(e));
    }

    private void updateExpCurveData() {
        List<XYChart.Series<Double, Double>> expCurveSeries = VESSeriesConverters.toExperimentalCurveSeriesAll(
                model.getExperimentalData(0)
        );
        ArrayList<XYChart.Series<Double, Double>> seriesList = new ArrayList<>(expCurveSeries);
        vesCurvesData.setValue(
                FXCollections.observableList(new ArrayList<>())
        );
        vesCurvesData.getValue().setAll(seriesList);
    }

    private void updateMisfitStacksData() {
        List<XYChart.Series<Double, Double>> misfitStacksSeriesList = MisfitStacksSeriesConverters.toMisfitStacksSeriesList(
                model.getExperimentalData(0), model.getModelData(0)
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

    public boolean isVesLineChartVisible() {
        return vesLineChartVisible.get();
    }

    public BooleanProperty vesLineChartVisibleProperty() {
        return vesLineChartVisible;
    }

    public boolean isMisfitStacksLineChartVisible() {
        return misfitStacksLineChartVisible.get();
    }

    public BooleanProperty misfitStacksLineChartVisibleProperty() {
        return misfitStacksLineChartVisible;
    }

    public String getVesText() {
        return vesText.get();
    }

    public StringProperty vesTextProperty() {
        return vesText;
    }

    public boolean isVesLegendsVisible() {
        return vesLegendsVisible.get();
    }

    public BooleanProperty vesLegendsVisibleProperty() {
        return vesLegendsVisible;
    }
}
