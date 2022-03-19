package ru.nucodelabs.gem.view.charts;

import com.google.inject.name.Named;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.ModelCurveDragger;
import ru.nucodelabs.gem.view.alerts.NoLibErrorAlert;
import ru.nucodelabs.gem.view.convert.VESSeriesConverters;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class VESCurvesController extends Controller {
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

    private final ObjectProperty<Picket> picket;

    private ResourceBundle uiProperties;
    private ModelCurveDragger modelCurveDragger;
    private boolean isDragging = false;

    @FXML
    private LineChart<Double, Double> lineChart;
    @FXML
    private NumberAxis lineChartXAxis;
    @FXML
    private NumberAxis lineChartYAxis;
    @Inject
    @Named("VESCurves")
    private ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> dataProperty;

    @Inject
    public VESCurvesController(ObjectProperty<Picket> picket) {
        this.picket = picket;
        picket.addListener((observable, oldValue, newValue) -> {
            if (isDragging) {
                updateTheoreticalCurve();
            } else {
                update();
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lineChart.dataProperty().bind(dataProperty);
        uiProperties = resources;
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

    protected void update() {
        updateExpCurves();
        updateTheoreticalCurve();
        updateModelCurve();
    }

    private void updateTheoreticalCurve() {
        XYChart.Series<Double, Double> theorCurveSeries = new XYChart.Series<>();

        if (picket.get().modelData() != null && picket.get().experimentalData() != null) {
            try {
                theorCurveSeries = VESSeriesConverters.toTheoreticalCurveSeries(
                        picket.get().experimentalData(), picket.get().modelData()
                );
            } catch (UnsatisfiedLinkError e) {
                new NoLibErrorAlert(e, getStage());
            }
        }

        theorCurveSeries.setName(uiProperties.getString("theorCurve"));
        dataProperty.get().set(THEOR_CURVE_SERIES_INDEX, theorCurveSeries);
    }

    private void updateModelCurve() {
        XYChart.Series<Double, Double> modelCurveSeries = new XYChart.Series<>();

        if (picket.get().modelData() != null) {
            modelCurveSeries = VESSeriesConverters.toModelCurveSeries(
                    picket.get().modelData()
            );
        }

        modelCurveSeries.setName(uiProperties.getString("modCurve"));
        dataProperty.get().set(MOD_CURVE_SERIES_INDEX, modelCurveSeries);

        addDraggingToModelCurveSeries(modelCurveSeries);
    }

    private void addDraggingToModelCurveSeries(XYChart.Series<Double, Double> modelCurveSeries) {
        if (picket.get().modelData() != null) {
            modelCurveSeries.getNode().setCursor(Cursor.HAND);
            modelCurveSeries.getNode().setOnMousePressed(e -> {
                isDragging = true;
                modelCurveDragger.resetStyle();
                modelCurveDragger.detectPoints(e);
                modelCurveDragger.setStyle();
            });
            modelCurveSeries.getNode().setOnMouseDragged(e -> {
                isDragging = true;
                picket.set(
                        new Picket(
                                picket.get().name(),
                                picket.get().experimentalData(),
                                modelCurveDragger.dragHandler(e, picket.get().modelData().clone()))
                );
            });
            modelCurveSeries.getNode().setOnMouseReleased(e -> {
                modelCurveDragger.resetStyle();
                isDragging = false;
            });
        }
    }

    private void updateExpCurves() {
        if (picket.get().experimentalData() != null) {
            XYChart.Series<Double, Double> expCurveSeries = VESSeriesConverters.toExperimentalCurveSeries(
                    picket.get().experimentalData()
            );
            expCurveSeries.setName(uiProperties.getString("expCurve"));
            XYChart.Series<Double, Double> errUpperExp = VESSeriesConverters.toErrorExperimentalCurveUpperBoundSeries(
                    picket.get().experimentalData()
            );
            errUpperExp.setName(uiProperties.getString("expCurveUpper"));
            XYChart.Series<Double, Double> errLowerExp = VESSeriesConverters.toErrorExperimentalCurveLowerBoundSeries(
                    picket.get().experimentalData()
            );
            errLowerExp.setName(uiProperties.getString("expCurveLower"));
            dataProperty.get().set(EXP_CURVE_SERIES_INDEX, expCurveSeries);
            dataProperty.get().set(EXP_CURVE_ERROR_UPPER_SERIES_INDEX, errUpperExp);
            dataProperty.get().set(EXP_CURVE_ERROR_LOWER_SERIES_INDEX, errLowerExp);
            if (picket.get().experimentalData() == null) {
                dataProperty.get().set(THEOR_CURVE_SERIES_INDEX, new XYChart.Series<>());
                dataProperty.get().set(MOD_CURVE_SERIES_INDEX, new XYChart.Series<>());
            }
        }
    }
}
