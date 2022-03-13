package ru.nucodelabs.gem.view.charts;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import ru.nucodelabs.gem.core.ViewService;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.MisfitStacksSeriesConverters;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static java.lang.Math.abs;

public class MisfitStacksController extends Controller {

    private Section section;
    private final ViewService viewService;

    @FXML
    private LineChart<Double, Double> lineChart;
    @FXML
    private NumberAxis lineChartXAxis;
    @FXML
    private NumberAxis lineChartYAxis;

    private ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> dataProperty;

    public MisfitStacksController(ViewService viewService) {
        this.viewService = viewService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataProperty = lineChart.dataProperty();
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public void update(int picketNumber) {
        List<XYChart.Series<Double, Double>> misfitStacksSeriesList = new ArrayList<>();

        if (section.getModelData(picketNumber) != null) {
            try {
                misfitStacksSeriesList = MisfitStacksSeriesConverters.toMisfitStacksSeriesList(
                        section.getExperimentalData(picketNumber), section.getModelData(picketNumber)
                );
            } catch (UnsatisfiedLinkError e) {
                viewService.alertNoLib(getStage(), e);
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
