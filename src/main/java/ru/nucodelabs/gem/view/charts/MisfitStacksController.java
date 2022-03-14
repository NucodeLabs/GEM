package ru.nucodelabs.gem.view.charts;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import ru.nucodelabs.gem.core.events.ModelDraggedEvent;
import ru.nucodelabs.gem.core.events.PicketSwitchEvent;
import ru.nucodelabs.gem.core.events.SectionChangeEvent;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.MisfitStacksSeriesConverters;
import ru.nucodelabs.gem.view.alerts.NoLibErrorAlert;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static java.lang.Math.abs;

public class MisfitStacksController extends Controller {

    private int currentPicket;
    private Section section;
    private EventBus eventBus;

    @FXML
    private LineChart<Double, Double> lineChart;
    @FXML
    private NumberAxis lineChartXAxis;
    @FXML
    private NumberAxis lineChartYAxis;

    private ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> dataProperty;

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    @Subscribe
    private void handlePicketSwitchEvent(PicketSwitchEvent picketSwitchEvent) {
        currentPicket = picketSwitchEvent.newPicketNumber();
        update();
    }

    @Subscribe
    private void handleModelDraggedEvent(ModelDraggedEvent event) {
        update();
    }

    @Subscribe
    private void handleSectionChangeEvent(SectionChangeEvent event) {
        update();
    }

    public void setSection(Section section) {
        this.section = section;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataProperty = lineChart.dataProperty();
    }

    private void update() {
        List<XYChart.Series<Double, Double>> misfitStacksSeriesList = new ArrayList<>();

        if (section.getPicket(currentPicket).modelData() != null) {
            try {
                misfitStacksSeriesList = MisfitStacksSeriesConverters.toMisfitStacksSeriesList(
                        section.getPicket(currentPicket).experimentalData(), section.getPicket(currentPicket).modelData()
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
