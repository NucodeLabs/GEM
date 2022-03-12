package ru.nucodelabs.gem.view.charts;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import ru.nucodelabs.gem.core.ViewService;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.ModelCurveDragger;
import ru.nucodelabs.gem.view.VESSeriesConverters;

import java.net.URL;
import java.util.ResourceBundle;

public class VESCurvesController extends Controller implements Initializable {
    /**
     * Constants
     */
    public static final int EXP_CURVE_SERIES_CNT = 3;
    public static final int THEOR_CURVE_SERIES_CNT = 4;
    public static final int MOD_CURVE_SERIES_CNT = 5;
    public static final int EXP_CURVE_SERIES_INDEX = 0;
    public static final int EXP_CURVE_ERROR_UPPER_SERIES_INDEX = 1;
    public static final int EXP_CURVE_ERROR_LOWER_SERIES_INDEX = 2;
    public static final int THEOR_CURVE_SERIES_INDEX = THEOR_CURVE_SERIES_CNT - 1;
    public static final int MOD_CURVE_SERIES_INDEX = MOD_CURVE_SERIES_CNT - 1;

    private Section section;
    private final ViewService viewService;
    private ResourceBundle uiProperties;
    private IntegerProperty currentPicket;
    private ModelCurveDragger modelCurveDragger;
    private Runnable updateOthers;

    @FXML
    private LineChart<Double, Double> lineChart;
    @FXML
    private NumberAxis lineChartXAxis;
    @FXML
    private NumberAxis lineChartYAxis;

    private ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> dataProperty;

    public VESCurvesController(ViewService viewService) {
        this.viewService = viewService;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public void setCurrentPicketProperty(IntegerProperty property) {
        currentPicket = property;
    }

    public void setUpdateView(Runnable updateOthers) {
        this.updateOthers = updateOthers;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        uiProperties = resources;
        dataProperty = lineChart.dataProperty();
        for (int i = 0; i < MOD_CURVE_SERIES_CNT; i++) {
            dataProperty.get().add(new XYChart.Series<>());
        }
        modelCurveDragger = new ModelCurveDragger(
                (pointInScene) -> new XYChart.Data<>(
                        (Double) lineChartXAxis.getValueForDisplay(
                                lineChartXAxis.sceneToLocal(pointInScene).getX()
                        ),
                        (Double) lineChartYAxis.getValueForDisplay(
                                lineChartYAxis.sceneToLocal(pointInScene).getY()
                        )
                ),
                dataProperty,
                MOD_CURVE_SERIES_INDEX
        );
    }

    @Override
    protected Stage getStage() {
        return (Stage) lineChart.getScene().getWindow();
    }

    public void updateTheoreticalCurve() {
        XYChart.Series<Double, Double> theorCurveSeries = new XYChart.Series<>();

        if (section.getModelData(currentPicket.get()) != null) {
            try {
                theorCurveSeries = VESSeriesConverters.toTheoreticalCurveSeries(
                        section.getExperimentalData(currentPicket.get()), section.getModelData(currentPicket.get())
                );
            } catch (UnsatisfiedLinkError e) {
                viewService.alertNoLib(getStage(), e);
            }
        }

        theorCurveSeries.setName(uiProperties.getString("theorCurve"));
        dataProperty.get().set(THEOR_CURVE_SERIES_INDEX, theorCurveSeries);
    }

    public void updateModelCurve() {
        XYChart.Series<Double, Double> modelCurveSeries = new XYChart.Series<>();

        if (section.getModelData(currentPicket.get()) != null) {
            modelCurveSeries = VESSeriesConverters.toModelCurveSeries(
                    section.getModelData(currentPicket.get())
            );
        }

        modelCurveSeries.setName(uiProperties.getString("modCurve"));
        dataProperty.get().set(MOD_CURVE_SERIES_INDEX, modelCurveSeries);

        if (section.getModelData(currentPicket.get()) != null) {
            addDraggingToModelCurveSeries(modelCurveSeries);
        }
    }

    private void addDraggingToModelCurveSeries(XYChart.Series<Double, Double> modelCurveSeries) {
        try {
            modelCurveDragger.mapModelData(section.getModelData(currentPicket.get()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        modelCurveSeries.getNode().setCursor(Cursor.HAND);
        modelCurveSeries.getNode().setOnMousePressed(e -> modelCurveDragger.lineToDragDetector(e));
        modelCurveSeries.getNode().setOnMouseDragged(e -> {
            modelCurveDragger.dragHandler(e);
            updateOthers.run();
        });
    }

    public void updateExpCurves() {
        XYChart.Series<Double, Double> expCurveSeries = VESSeriesConverters.toExperimentalCurveSeries(
                section.getExperimentalData(currentPicket.get())
        );
        expCurveSeries.setName(uiProperties.getString("expCurve"));
        XYChart.Series<Double, Double> errUpperExp = VESSeriesConverters.toErrorExperimentalCurveUpperBoundSeries(
                section.getExperimentalData(currentPicket.get())
        );
        errUpperExp.setName(uiProperties.getString("expCurveUpper"));
        XYChart.Series<Double, Double> errLowerExp = VESSeriesConverters.toErrorExperimentalCurveLowerBoundSeries(
                section.getExperimentalData(currentPicket.get())
        );
        errLowerExp.setName(uiProperties.getString("expCurveLower"));
        dataProperty.get().set(EXP_CURVE_SERIES_INDEX, expCurveSeries);
        dataProperty.get().set(EXP_CURVE_ERROR_UPPER_SERIES_INDEX, errUpperExp);
        dataProperty.get().set(EXP_CURVE_ERROR_LOWER_SERIES_INDEX, errLowerExp);
        if (section.getModelData(currentPicket.get()) == null) {
            dataProperty.get().set(THEOR_CURVE_SERIES_INDEX, new XYChart.Series<>());
            dataProperty.get().set(MOD_CURVE_SERIES_INDEX, new XYChart.Series<>());
        }
    }
}
