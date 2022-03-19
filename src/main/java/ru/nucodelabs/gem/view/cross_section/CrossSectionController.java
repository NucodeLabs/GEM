package ru.nucodelabs.gem.view.cross_section;

import io.reactivex.rxjava3.subjects.Subject;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.core.events.ViewEvent;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.view.AbstractSectionController;
import ru.nucodelabs.gem.view.convert.CrossSectionConverters;

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
    StackedBarChart<String, Double> sectionBarChart;

    private ResourceBundle uiProperties;
    private ObjectProperty<ObservableList<XYChart.Series<String, Double>>> dataProperty;

    @Inject
    public CrossSectionController(Subject<ViewEvent> viewEvents, Section section) {
        super(viewEvents, section);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        uiProperties = resources;
        dataProperty = sectionBarChart.dataProperty();
    }

    public void update() {
        ArrayList<Boolean> blanks = new ArrayList<>();
        int maxLayers = 0;

        for (Picket p : section.getPickets()) {
            if (p.modelData() == null) {
                blanks.add(false);
            } else {
                blanks.add(true);
            }
        }

        for (int i = 0; i < section.getPicketsCount(); i++) {
            if (blanks.get(i) && maxLayers < section.getModelData(i).getSize()) {
                maxLayers = section.getModelData(i).getSize();
            }
        }

        if (maxLayers == 0) {
            maxLayers = 1;
        }

        for (int i = 0; i < maxLayers; i++) {
            XYChart.Series<String, Double> tempSeries = CrossSectionConverters.getLayerOfPowers(section.getPickets(), i);
            tempSeries.setName(((Integer) i).toString());

            dataProperty.get().add(new XYChart.Series<>());
            dataProperty.get().set(i, tempSeries);
        }
    }

    protected Stage getStage() {
        return (Stage) sectionBarChart.getScene().getWindow();
    }
}
