package ru.nucodelabs.gem.view.cross_section;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.view.AbstractController;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CrossSectionController extends AbstractController {

    private ResourceBundle uiProperties;
    private int maxLayerCount;
    public int picketCount;
    private final ObservableList<Picket> picketObservableList;

    @FXML
    StackedBarChart<String, Number> sectionStackedBarChart;
    @FXML
    public CategoryAxis sectionStackedBarChartXAxis;
    @FXML
    public NumberAxis sectionStackedBarChartYAxis;

    @Inject
    private ObjectProperty<ObservableList<XYChart.Series<String, Number>>> dataProperty;

    @Inject
    public CrossSectionController(ObservableList<Picket> picketObservableList) {
        this.picketObservableList = picketObservableList;
        picketObservableList.addListener((ListChangeListener<? super Picket>) c -> {
            if (c.next()) {
                update();
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        uiProperties = resources;
        sectionStackedBarChart.dataProperty().bind(dataProperty);

    }

    public void update() {
        List<XYChart.Series<String, Number>> seriesList = new ArrayList<>();
        sectionStackedBarChartXAxis.setCategories(FXCollections.observableArrayList(CrossSectionConverters.makeCategories(picketObservableList)));

        if (picketObservableList != null) {
            seriesList = CrossSectionConverters.getLayersOfPowers(picketObservableList);
        }
        dataProperty.get().clear();
        dataProperty.get().addAll(seriesList);

        //System.out.println(sectionStackedBarChartXAxis.getCategories().toString());
        //System.out.println(sectionStackedBarChart.dataProperty().toString());
    }

    protected Stage getStage() {
        return (Stage) sectionStackedBarChart.getScene().getWindow();
    }

    public int getPicketCount() {
        return picketCount;
    }
}
