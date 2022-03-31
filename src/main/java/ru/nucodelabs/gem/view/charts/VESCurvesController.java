package ru.nucodelabs.gem.view.charts;

import com.google.inject.name.Named;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import ru.nucodelabs.algorithms.charts.PointsFactory;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.app.AppService;
import ru.nucodelabs.gem.app.annotation.Subject;
import ru.nucodelabs.gem.app.operation.ModelCurveDraggedOperation;
import ru.nucodelabs.gem.view.AbstractController;
import ru.nucodelabs.gem.view.AlertsFactory;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

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
    private AlertsFactory alertsFactory;
    @Inject
    private AppService appService;
    @Inject
    private ModelCurveDraggedOperation.Factory operationFactory;
    @Inject
    @Subject
    private ObservableList<Picket> picketObservableList;

    @Inject
    public VESCurvesController(ObjectProperty<Picket> picket) {
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
                MOD_CURVE_SERIES_INDEX
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
            PointsFactory pointsFactory = PointsFactory.theoreticalCurvePointsFactory(
                    picket.get().experimentalData(), picket.get().modelData());
            theorCurveSeries.getData().addAll(
                    pointsFactory.log10Points().stream()
                            .map(point -> new XYChart.Data<>(point.x(), point.y()))
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

        PointsFactory pointsFactory = PointsFactory.modelCurvePointsFactory(picket.get().modelData());
        modelCurveSeries.getData().addAll(
                pointsFactory.log10Points().stream()
                        .map(point -> new XYChart.Data<>(point.x(), point.y()))
                        .toList()
        );

        modelCurveSeries.setName(uiProperties.getString("modCurve"));
        dataProperty.get().set(MOD_CURVE_SERIES_INDEX, modelCurveSeries);

        addDraggingToModelCurveSeries(modelCurveSeries);
    }

    private void addDraggingToModelCurveSeries(XYChart.Series<Double, Double> modelCurveSeries) {
        AtomicReference<List<Picket>> beforeDragState = new AtomicReference<>();
        modelCurveSeries.getNode().setCursor(Cursor.HAND);
        modelCurveSeries.getNode().setOnMousePressed(e -> {
            beforeDragState.set(List.copyOf(picketObservableList));
            isDragging = true;
            lineChart.setAnimated(false);
            lineChartYAxis.setAutoRanging(false);
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
            appService.execute(operationFactory.create(beforeDragState.get(), picket.get().modelData()));
            modelCurveDragger.resetStyle();
            isDragging = false;
            lineChart.setAnimated(true);
            lineChartYAxis.setAutoRanging(true);
        });
    }

    private void updateExpCurves() {
        PointsFactory pointsFactory = PointsFactory.experimentalCurvePointsFactory(picket.get().experimentalData());
        XYChart.Series<Double, Double> expCurveSeries = new XYChart.Series<>(
                FXCollections.observableList(
                        pointsFactory.log10Points().stream()
                                .map(point -> new XYChart.Data<>(point.x(), point.y()))
                                .toList()
                )
        );
        expCurveSeries.setName(uiProperties.getString("expCurve"));

        pointsFactory = PointsFactory.experimentalCurveUpperBoundErrorPointsFactory(picket.get().experimentalData());
        XYChart.Series<Double, Double> errUpperExp = new XYChart.Series<>(
                FXCollections.observableList(
                        pointsFactory.log10Points().stream()
                                .map(point -> new XYChart.Data<>(point.x(), point.y()))
                                .toList()
                )
        );
        errUpperExp.setName(uiProperties.getString("expCurveUpper"));

        pointsFactory = PointsFactory.experimentalCurveLowerBoundErrorPointsFactory(picket.get().experimentalData());
        XYChart.Series<Double, Double> errLowerExp = new XYChart.Series<>(
                FXCollections.observableList(
                        pointsFactory.log10Points().stream()
                                .map(point -> new XYChart.Data<>(point.x(), point.y()))
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
