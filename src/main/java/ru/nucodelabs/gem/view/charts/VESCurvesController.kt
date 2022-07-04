package ru.nucodelabs.gem.view.charts;

import com.google.inject.name.Named;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import ru.nucodelabs.algorithms.charts.VesCurvesConverter;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.Section;
import ru.nucodelabs.gem.app.model.SectionManager;
import ru.nucodelabs.gem.app.snapshot.HistoryManager;
import ru.nucodelabs.gem.view.AbstractController;
import ru.nucodelabs.gem.view.AlertsFactory;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

import static java.lang.Math.log10;

public class VESCurvesController extends AbstractController {
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

    private final ObservableObjectValue<Picket> picket;

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
    private AlertsFactory alertsFactory;
    @Inject
    private SectionManager sectionManager;
    @Inject
    private HistoryManager<Section> historyManager;
    @Inject
    private VesCurvesConverter vesCurvesConverter;

    @Inject
    public VESCurvesController(ObservableObjectValue<Picket> picket) {
        this.picket = picket;
        this.picket.addListener((observable, oldValue, newValue) -> {
            if (isDragging) {
                updateTheoreticalCurve();
            } else {
                if (newValue != null) {
                    update();
                }
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
                MOD_CURVE_SERIES_INDEX,
                1
        );
    }

    @Override
    protected Stage getStage() {
        return (Stage) lineChart.getScene().getWindow();
    }

    protected void update() {
        lineChart.setAnimated(false);
        lineChartYAxis.setAutoRanging(true);
        updateExpCurves();
        updateTheoreticalCurve();
        updateModelCurve();
    }

    private void updateTheoreticalCurve() {
        XYChart.Series<Double, Double> theorCurveSeries = new XYChart.Series<>();

        try {
            theorCurveSeries.getData().addAll(
                    vesCurvesConverter.theoreticalCurveOf(
                                    picket.get().getExperimentalData(), picket.get().getModelData())
                            .stream()
                            .map(point -> new XYChart.Data<>(
                                    log10(point.x()), log10(point.y())))
                            .toList()
            );
        } catch (UnsatisfiedLinkError e) {
            alertsFactory.unsatisfiedLinkErrorAlert(e, getStage());
        }

        theorCurveSeries.setName(uiProperties.getString("theorCurve"));
        dataProperty.get().set(THEOR_CURVE_SERIES_INDEX, theorCurveSeries);
    }

    private void updateModelCurve() {
        XYChart.Series<Double, Double> modelCurveSeries = new XYChart.Series<>();

        modelCurveSeries.getData().addAll(
                vesCurvesConverter.modelCurveOf(picket.get().getModelData())
                        .stream()
                        .map(point -> new XYChart.Data<>(
                                log10(point.x()), log10(point.y())))
                        .toList()
        );

        modelCurveSeries.setName(uiProperties.getString("modCurve"));
        dataProperty.get().set(MOD_CURVE_SERIES_INDEX, modelCurveSeries);

        addDraggingToModelCurveSeries(modelCurveSeries);
    }

    private void addDraggingToModelCurveSeries(XYChart.Series<Double, Double> modelCurveSeries) {
        modelCurveSeries.getNode().setCursor(Cursor.HAND);
        modelCurveSeries.getNode().setOnMousePressed(e -> {
            modelCurveSeries.getNode().requestFocus();
            isDragging = true;
            lineChart.setAnimated(false);
            lineChartYAxis.setAutoRanging(false);
            modelCurveDragger.detectPoints(e);
            modelCurveDragger.setStyle();
        });
        modelCurveSeries.getNode().setOnMouseDragged(e -> {
            isDragging = true;
            sectionManager.update(
                    picket.get().withModelData(
                            modelCurveDragger.dragHandler(e, picket.get().getModelData())));
        });
        modelCurveSeries.getNode().setOnMouseReleased(e -> {
            historyManager.snapshot();
            modelCurveDragger.resetStyle();
            isDragging = false;
            lineChart.setAnimated(true);
            lineChartYAxis.setAutoRanging(true);
        });
    }

    private void updateExpCurves() {
        XYChart.Series<Double, Double> expCurveSeries = new XYChart.Series<>(
                FXCollections.observableList(
                        vesCurvesConverter.experimentalCurveOf(picket.get().getExperimentalData())
                                .stream()
                                .map(point -> new XYChart.Data<>(
                                        log10(point.x()), log10(point.y())))
                                .toList()
                )
        );
        expCurveSeries.setName(uiProperties.getString("expCurve"));

        XYChart.Series<Double, Double> errUpperExp = new XYChart.Series<>(
                FXCollections.observableList(
                        vesCurvesConverter.experimentalCurveErrorBoundOf(picket.get().getExperimentalData(), VesCurvesConverter.BoundType.UPPER_BOUND)
                                .stream()
                                .map(point -> new XYChart.Data<>(
                                        log10(point.x()), log10(point.y())))
                                .toList()
                )
        );
        errUpperExp.setName(uiProperties.getString("expCurveUpper"));

        XYChart.Series<Double, Double> errLowerExp = new XYChart.Series<>(
                FXCollections.observableList(
                        vesCurvesConverter.experimentalCurveErrorBoundOf(picket.get().getExperimentalData(), VesCurvesConverter.BoundType.LOWER_BOUND)
                                .stream()
                                .map(point -> new XYChart.Data<>(
                                        log10(point.x()), log10(point.y())))
                                .toList()
                )
        );
        errLowerExp.setName(uiProperties.getString("expCurveLower"));
        dataProperty.get().set(EXP_CURVE_SERIES_INDEX, expCurveSeries);
        dataProperty.get().set(EXP_CURVE_ERROR_UPPER_SERIES_INDEX, errUpperExp);
        dataProperty.get().set(EXP_CURVE_ERROR_LOWER_SERIES_INDEX, errLowerExp);
    }

    public BooleanProperty legendVisibleProperty() {
        return lineChart.legendVisibleProperty();
    }
}
