package ru.nucodelabs.gem.view.cross_section;

import io.reactivex.rxjava3.subjects.Subject;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import ru.nucodelabs.gem.core.events.ViewEvent;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.view.AbstractSectionController;
import ru.nucodelabs.gem.view.convert.GeoSectionConverters;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CrossSectionController extends AbstractSectionController {

    @FXML
    public CategoryAxis sectionBarChartXAxis;
    @FXML
    public NumberAxis sectionBarChartYAxis;
    @FXML
    StackedBarChart<Double, Double> sectionBarChart;

    private ResourceBundle uiProperties;
    private ObjectProperty<ObservableList<XYChart.Series<String, Double>>> dataProperty;

    @Inject
    public CrossSectionController(Subject<ViewEvent> viewEvents, Section section) {
        super(viewEvents, section);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        uiProperties = resources;
        dataProperty = new SimpleObjectProperty<>();        //???
    }

    public void update() {
        ArrayList<XYChart.Series<String, Double>> barSeries = new ArrayList<>();
        int maxLayers = section.getPickets().stream()
                .max(
                        (picket1, picket2) -> Math.max(
                                picket1.modelData().getSize(),
                                picket2.modelData().getSize())
                ).get().modelData().getSize();

        for (int i = 0; i < maxLayers; i++) {
            barSeries.add(GeoSectionConverters
                    .getLayerOfPowers(
                            section.getPickets(), i));
        }

        dataProperty.get().setAll(barSeries);
    }

    protected Stage getStage() {
        return (Stage) sectionBarChart.getScene().getWindow();
    }
}
