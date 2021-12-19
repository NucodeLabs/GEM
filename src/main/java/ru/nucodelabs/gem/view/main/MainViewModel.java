package ru.nucodelabs.gem.view.main;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
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
import ru.nucodelabs.gem.view.MisfitStacksSeriesConverter;
import ru.nucodelabs.gem.view.VESSeriesConverter;
import ru.nucodelabs.mvvm.ViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class MainViewModel extends ViewModel<VESDataModel> {

    /**
     * <h3>Helpers</h3>
     */
    private final VESSeriesConverter vesSeriesConverter;
    private final MisfitStacksSeriesConverter misfitStacksSeriesConverter;

    /**
     * <h3>Properties</h3>
     */
    private final ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> vesCurvesData;
    private final BooleanProperty vesLineChartVisibility;
    private final StringProperty vesText;
    private final ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> misfitStacksData;
    private final BooleanProperty misfitStacksLineChartVisibility;
    private final BooleanProperty menuFileMODDisabled;

    /**
     * <h3>Constructor</h3>
     * Initialization
     *
     * @param vesDataModel VES Data Model
     * @param viewManager  View Manager
     */
    public MainViewModel(VESDataModel vesDataModel, ViewManager viewManager) {
        super(vesDataModel, viewManager);

        vesSeriesConverter = new VESSeriesConverter();
        misfitStacksSeriesConverter = new MisfitStacksSeriesConverter();

        menuFileMODDisabled = new SimpleBooleanProperty(true);

        vesLineChartVisibility = new SimpleBooleanProperty(false);
        vesCurvesData = new SimpleObjectProperty<>();
        vesText = new SimpleStringProperty("");

        misfitStacksLineChartVisibility = new SimpleBooleanProperty(false);
        misfitStacksData = new SimpleObjectProperty<>();
    }

    private void alertExperimentalDataIsUnsafe() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Режим совместимости");
        alert.setHeaderText("STT и EXP содержат разное количество строк");
        alert.setContentText("Будет отображаться минимально возможное число данных");
        alert.show();
    }

    private void alertFileNotFound(FileNotFoundException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Файл не найден!");
        alert.setContentText(e.getMessage());
        alert.show();
    }

    private void alertNoLib(UnsatisfiedLinkError e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Невозможно отрисовать график");
        alert.setHeaderText("Отсутствует библиотека");
        alert.setContentText(e.getMessage());
        alert.show();
    }

    public boolean importEXPSTT(Scene sceneForFileChooser) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите файл полевых данных для интерпретации");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("EXP - Полевые данные", "*.EXP", "*.exp")
        );
        File file = chooser.showOpenDialog(sceneForFileChooser.getWindow());
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

        misfitStacksLineChartVisibility.setValue(false);
        vesLineChartVisibility.setValue(true);
        return true;
    }

    public boolean importMOD(Scene sceneForFileChooser) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Выберите файл модели");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MOD - Данные модели", "*.MOD", "*.mod")
        );
        File file = chooser.showOpenDialog(sceneForFileChooser.getWindow());
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
        updateMisfitStacksData();
        misfitStacksLineChartVisibility.setValue(true);
        return true;
    }

    private void updateTheoreticalCurve() {
        XYChart.Series<Double, Double> theorCurveSeries = vesSeriesConverter.toTheoreticalCurveSeries(
                model.getExperimentalData(0), model.getModelData(0)
        );
        vesCurvesData.getValue().add(theorCurveSeries);
    }

    private void updateModelCurve() {
        XYChart.Series<Double, Double> modelCurveSeries = vesSeriesConverter.toModelCurveSeries(
                model.getModelData(0)
        );
        vesCurvesData.getValue().add(modelCurveSeries);
    }

    private void updateExpCurveData() {
        List<XYChart.Series<Double, Double>> expCurveSeries = vesSeriesConverter.toExperimentalCurveSeriesAll(
                model.getExperimentalData(0)
        );
        ArrayList<XYChart.Series<Double, Double>> seriesList = new ArrayList<>(expCurveSeries);
        vesCurvesData.setValue(
                FXCollections.observableList(seriesList)
        );
    }

    private void updateMisfitStacksData() {
        List<XYChart.Series<Double, Double>> misfitStacksSeriesList = misfitStacksSeriesConverter.toMisfitStacksSeriesList(
                model.getExperimentalData(0), model.getModelData(0)
        );
        misfitStacksData.setValue(
                FXCollections.observableList(misfitStacksSeriesList)
        );
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
        return vesLineChartVisibility.get();
    }

    public BooleanProperty vesLineChartVisibilityProperty() {
        return vesLineChartVisibility;
    }

    public boolean isMisfitStacksLineChartVisible() {
        return misfitStacksLineChartVisibility.get();
    }

    public BooleanProperty misfitStacksLineChartVisibilityProperty() {
        return misfitStacksLineChartVisibility;
    }

    public String getVesText() {
        return vesText.get();
    }

    public StringProperty vesTextProperty() {
        return vesText;
    }
}
