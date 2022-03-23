package ru.nucodelabs.gem.view.charts;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import ru.nucodelabs.algorithms.charts.MisfitValuesFactory;
import ru.nucodelabs.algorithms.charts.Point;
import ru.nucodelabs.algorithms.charts.PointsFactory;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.alerts.ExceptionAlert;
import ru.nucodelabs.gem.view.alerts.NoLibErrorAlert;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static java.lang.Math.abs;

public class MisfitStacksController extends Controller {


    private final ObservableObjectValue<Picket> picket;
    @FXML
    private LineChart<Double, Double> lineChart;
    @FXML
    private NumberAxis lineChartXAxis;
    @FXML
    private NumberAxis lineChartYAxis;
    @Inject
    private ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> dataProperty;

    /**
     * Отображает отклонение модельных данных от экспериментальных для конкретного пикета.
     * Если меняются только модельные данные, обновляется.
     *
     * @param picket пикет
     */
    @Inject
    public MisfitStacksController(
            ObservableObjectValue<Picket> picket) {
        this.picket = picket;
        picket.addListener((observable, oldValue, newValue) -> update());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lineChart.dataProperty().bind(dataProperty);
    }

    protected void update() {
        List<XYChart.Series<Double, Double>> misfitStacksSeriesList = new ArrayList<>();

        try {
            List<Double> values = MisfitValuesFactory.getDefaultMisfitValuesFactory().apply(picket.get().experimentalData(), picket.get().modelData());
            List<Point> expPoints = PointsFactory.experimentalCurvePointsFactory(picket.get().experimentalData()).log10Points();
            misfitStacksSeriesList = FXCollections.observableList(new ArrayList<>());

            if (picket.get().experimentalData().size() != 0
                    && picket.get().modelData().size() != 0) {
                if (values.size() != expPoints.size()) {
                    throw new IllegalStateException();
                } else {
                    for (int i = 0; i < expPoints.size(); i++) {
                        misfitStacksSeriesList.add(new XYChart.Series<>(
                                FXCollections.observableList(List.of(
                                        new XYChart.Data<>(expPoints.get(i).x(), 0d),
                                        new XYChart.Data<>(expPoints.get(i).x(), values.get(i))
                                ))
                        ));
                    }
                }
            }
        } catch (UnsatisfiedLinkError e) {
            new NoLibErrorAlert(e, getStage()).show();
        } catch (IllegalStateException e) {
            new ExceptionAlert(e, getStage()).show();
        }

        dataProperty.get().clear();
        dataProperty.get().addAll(misfitStacksSeriesList);
        colorizeMisfitStacksSeries();
    }

    private void colorizeMisfitStacksSeries() {
        var data = dataProperty.get();
        for (var series : data) {
            var nonZeroPoint = series.getData().get(1);
            if (abs(nonZeroPoint.getYValue()) < 100f) {
                series.getNode().setStyle("-fx-stroke: LimeGreen;");
                nonZeroPoint.getNode().lookup(".chart-line-symbol").setStyle("-fx-background-color: LimeGreen");
                var zeroPoint = series.getData().get(0);
                zeroPoint.getNode().lookup(".chart-line-symbol").setStyle("-fx-background-color: LimeGreen");
            }
        }
    }

    @Override
    protected Stage getStage() {
        return (Stage) lineChartXAxis.getScene().getWindow();
    }
}
