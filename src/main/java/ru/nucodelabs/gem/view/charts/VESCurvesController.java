package ru.nucodelabs.gem.view.charts;

import com.google.common.eventbus.Subscribe;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import ru.nucodelabs.gem.core.events.ModelDraggedEvent;
import ru.nucodelabs.gem.core.events.ViewEvent;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.view.AbstractSectionController;
import ru.nucodelabs.gem.view.ModelCurveDragger;
import ru.nucodelabs.gem.view.alerts.NoLibErrorAlert;
import ru.nucodelabs.gem.view.convert.VESSeriesConverters;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class VESCurvesController extends AbstractSectionController {
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

    private ResourceBundle uiProperties;
    private ModelCurveDragger modelCurveDragger;

    @FXML
    private LineChart<Double, Double> lineChart;
    @FXML
    private NumberAxis lineChartXAxis;
    @FXML
    private NumberAxis lineChartYAxis;

    private ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> dataProperty;

    @Inject
    public VESCurvesController(Subject<ViewEvent> viewEventSubject, Section section) {
        super(viewEventSubject, section);
        this.viewEvents
                .filter(e -> e instanceof ModelDraggedEvent)
                .cast(ModelDraggedEvent.class)
                .subscribe(this::handleModelDraggedEvent);
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

    @Subscribe
    private void handleModelDraggedEvent(ModelDraggedEvent event) {
        updateTheoreticalCurve();
    }

    @Override
    protected void update() {
        updateExpCurves();
        updateTheoreticalCurve();
        updateModelCurve();
    }

    private void updateTheoreticalCurve() {
        XYChart.Series<Double, Double> theorCurveSeries = new XYChart.Series<>();

        if (section.getModelData(currentPicket) != null) {
            try {
                theorCurveSeries = VESSeriesConverters.toTheoreticalCurveSeries(
                        section.getExperimentalData(currentPicket), section.getModelData(currentPicket)
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

        if (section.getModelData(currentPicket) != null) {
            modelCurveSeries = VESSeriesConverters.toModelCurveSeries(
                    section.getModelData(currentPicket)
            );
        }

        modelCurveSeries.setName(uiProperties.getString("modCurve"));
        dataProperty.get().set(MOD_CURVE_SERIES_INDEX, modelCurveSeries);

        if (section.getModelData(currentPicket) != null) {
            addDraggingToModelCurveSeries(modelCurveSeries);
        }
    }

    private void addDraggingToModelCurveSeries(XYChart.Series<Double, Double> modelCurveSeries) {
        try {
            modelCurveDragger.mapModelData(section.getModelData(currentPicket));
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
            viewEvents.onNext(new ModelDraggedEvent());
        });
    }

    private void updateExpCurves() {
        XYChart.Series<Double, Double> expCurveSeries = VESSeriesConverters.toExperimentalCurveSeries(
                section.getExperimentalData(currentPicket)
        );
        expCurveSeries.setName(uiProperties.getString("expCurve"));
        XYChart.Series<Double, Double> errUpperExp = VESSeriesConverters.toErrorExperimentalCurveUpperBoundSeries(
                section.getExperimentalData(currentPicket)
        );
        errUpperExp.setName(uiProperties.getString("expCurveUpper"));
        XYChart.Series<Double, Double> errLowerExp = VESSeriesConverters.toErrorExperimentalCurveLowerBoundSeries(
                section.getExperimentalData(currentPicket)
        );
        errLowerExp.setName(uiProperties.getString("expCurveLower"));
        dataProperty.get().set(EXP_CURVE_SERIES_INDEX, expCurveSeries);
        dataProperty.get().set(EXP_CURVE_ERROR_UPPER_SERIES_INDEX, errUpperExp);
        dataProperty.get().set(EXP_CURVE_ERROR_LOWER_SERIES_INDEX, errLowerExp);
        if (section.getExperimentalData(currentPicket) == null) {
            dataProperty.get().set(THEOR_CURVE_SERIES_INDEX, new XYChart.Series<>());
            dataProperty.get().set(MOD_CURVE_SERIES_INDEX, new XYChart.Series<>());
        }
    }
}
