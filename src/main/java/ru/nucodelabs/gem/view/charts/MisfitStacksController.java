package ru.nucodelabs.gem.view.charts;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.alerts.NoLibErrorAlert;
import ru.nucodelabs.gem.view.convert.MisfitStacksSeriesConverters;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static java.lang.Math.abs;

public class MisfitStacksController extends Controller {


    private final ObservableObjectValue<Picket> picketObservable;
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
     * @param picketObservable пикет
     */
    @Inject
    public MisfitStacksController(
            ObservableObjectValue<Picket> picketObservable) {
        this.picketObservable = picketObservable;
        picketObservable.addListener((observable, oldValue, newValue) -> {
            update();
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lineChart.dataProperty().bind(dataProperty);
    }

    protected void update() {
        List<XYChart.Series<Double, Double>> misfitStacksSeriesList = new ArrayList<>();

        if (picketObservable.get().modelData() != null && picketObservable.get().experimentalData() != null) {
            try {
                misfitStacksSeriesList = MisfitStacksSeriesConverters.toMisfitStacksSeriesList(
                        picketObservable.get().experimentalData(), picketObservable.get().modelData()
                );
            } catch (UnsatisfiedLinkError e) {
                new NoLibErrorAlert(e, getStage()).show();
            }
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
