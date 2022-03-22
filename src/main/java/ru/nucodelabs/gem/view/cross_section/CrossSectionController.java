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
import ru.nucodelabs.gem.view.Controller;
import ru.nucodelabs.gem.view.convert.CrossSectionConverters;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class CrossSectionController extends Controller {

    private ResourceBundle uiProperties;
    private final ObservableList<Picket> picketObservableList;

    @FXML
    public CategoryAxis sectionStackedBarChartXAxis;
    @FXML
    public NumberAxis sectionStackedBarChartYAxis;
    @FXML
    StackedBarChart<String, Number> sectionStackedBarChart;

    @Inject
    @Named("CrossSection")
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


        /*for (Picket p : picketObservableList) {
            categories.add(p.name());
        }

        ((CategoryAxis)sectionStackedBarChart.getXAxis()).setCategories(FXCollections.observableArrayList(categories));*/



        //Создаются Series из всех еще непроверенных нижних слоев пикетов.
        //Двигаясь снизу вверх получается несколько "слоев" слоев из которых можно будет потом сложить StackedBarChart.
        //Все null -> 10.0 (в целях тестирования)

        List<XYChart.Series<String, Number>> seriesList = makeSeriesList();
        for (int i = 0; i < maxLayers; i++) {
            newList.add(CrossSectionConverters.getLayerOfPowers(picketObservableList, i));
            newList.get(i).setName(((Integer) i).toString());
        }

        dataProperty.set(FXCollections.observableArrayList(newList));
    }

    private List<XYChart.Series<String, Number>> makeSeriesList() {
        List<XYChart.Series<String, Number>> seriesList = new ArrayList<>();
        int layerNum = 0;
        for (Picket picket : picketObservableList) {
            if (picket == null) {
                seriesList.add(CrossSectionConverters.getLayerOfPowers(picketObservableList, layerNum++));
            }
        }
        //Находится наибольшее число слоев среди всех пикетов разреза

    }

    private int findMaxLayers() {
        int maxLayers = 1;
        for (Picket picket : picketObservableList) {
            if (picket != null) {
                int picketSize = picket.modelData().getSize();
                if (maxLayers < picketSize) {
                    maxLayers = picketSize;
                }
            }
        }
        return maxLayers;
    }

    protected Stage getStage() {
        return (Stage) sectionStackedBarChart.getScene().getWindow();
    }
}
