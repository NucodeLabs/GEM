package ru.nucodelabs.gem.view.charts;

import com.google.inject.name.Named;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.ModelData;
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
    private final Subject<ModelData> modelDataSubject;

    private ResourceBundle uiProperties;
    private ModelCurveDragger modelCurveDragger;
    private Picket picket;

    @FXML
    private LineChart<Double, Double> lineChart;
    @FXML
    private NumberAxis lineChartXAxis;
    @FXML
    private NumberAxis lineChartYAxis;
    @Inject
    @Named("VESCurves")
    private ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> dataProperty;

    /**
     * Отображает интерпретацию ВЭЗ для конкретного пикета, модифицирует модельные данные драг-н-дропом.
     * При смене пикета, обновляется.
     * Пишет изменения в модельные данные, сам на них не реагирует.
     *
     * @param picketObservable пикет
     * @param modelDataSubject модельные данные
     */
    @Inject
    public VESCurvesController(
            Observable<Picket> picketObservable,
            Subject<ModelData> modelDataSubject) {
        this.modelDataSubject = modelDataSubject;
        picketObservable
                .subscribe(picket1 -> {
                    picket = picket1;
                    update();
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

        if (picket.modelData() != null) {
            try {
                theorCurveSeries = VESSeriesConverters.toTheoreticalCurveSeries(
                        picket.experimentalData(), picket.modelData()
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

        if (picket.modelData() != null) {
            modelCurveSeries = VESSeriesConverters.toModelCurveSeries(
                    picket.modelData()
            );
        }

        modelCurveSeries.setName(uiProperties.getString("modCurve"));
        dataProperty.get().set(MOD_CURVE_SERIES_INDEX, modelCurveSeries);

        if (picket.modelData() != null) {
            addDraggingToModelCurveSeries(modelCurveSeries);
        }
    }

    private void addDraggingToModelCurveSeries(XYChart.Series<Double, Double> modelCurveSeries) {
        try {
            modelCurveDragger.mapModelData(picket.modelData());
        } catch (Exception e) {
            e.printStackTrace();
        }
        modelCurveSeries.getNode().setCursor(Cursor.HAND);
        modelCurveSeries.getNode().setOnMousePressed(e -> {
            modelCurveDragger.resetStyle();
            modelCurveDragger.lineToDragDetector(e);
            modelCurveDragger.setStyle();
        });
        modelCurveSeries.getNode().setOnMouseDragged(e -> {
            modelCurveDragger.dragHandler(e);
            modelDataSubject.onNext(picket.modelData());
            updateTheoreticalCurve();
        });
    }

    private void updateExpCurves() {
        XYChart.Series<Double, Double> expCurveSeries = VESSeriesConverters.toExperimentalCurveSeries(
                picket.experimentalData()
        );
        expCurveSeries.setName(uiProperties.getString("expCurve"));
        XYChart.Series<Double, Double> errUpperExp = VESSeriesConverters.toErrorExperimentalCurveUpperBoundSeries(
                picket.experimentalData()
        );
        errUpperExp.setName(uiProperties.getString("expCurveUpper"));
        XYChart.Series<Double, Double> errLowerExp = VESSeriesConverters.toErrorExperimentalCurveLowerBoundSeries(
                picket.experimentalData()
        );
        errLowerExp.setName(uiProperties.getString("expCurveLower"));
        dataProperty.get().set(EXP_CURVE_SERIES_INDEX, expCurveSeries);
        dataProperty.get().set(EXP_CURVE_ERROR_UPPER_SERIES_INDEX, errUpperExp);
        dataProperty.get().set(EXP_CURVE_ERROR_LOWER_SERIES_INDEX, errLowerExp);
        if (picket.experimentalData() == null) {
            dataProperty.get().set(THEOR_CURVE_SERIES_INDEX, new XYChart.Series<>());
            dataProperty.get().set(MOD_CURVE_SERIES_INDEX, new XYChart.Series<>());
        }
    }
}
