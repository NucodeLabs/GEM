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
        ArrayList<Boolean> blanks = new ArrayList<>();
        ArrayList<String> categories = new ArrayList<>();
        int maxLayers = 0;

        /*for (Picket p : picketObservableList) {
            categories.add(p.name());
        }

        ((CategoryAxis)sectionStackedBarChart.getXAxis()).setCategories(FXCollections.observableArrayList(categories));*/

        //Помечаются валидные и null модели пикетов
        for (Picket p : picketObservableList) {
            if (p.modelData() == null) {
                blanks.add(false);
            } else {
                blanks.add(true);
            }
        }

        //Находится наибольшее число слоев среди всех пикетов разреза
        for (int i = 0; i < picketObservableList.size(); i++) {
            if (blanks.get(i)) {
                int picketSize = picketObservableList.get(i).modelData().getSize();
                if (maxLayers < picketSize) {
                    maxLayers = picketSize;
                }
            }
        }

        if (maxLayers == 0) {
            maxLayers = 1;
        }

        //Создаются Series из всех еще непроверенных нижних слоев пикетов.
        //Двигаясь снизу вверх получается несколько "слоев" слоев из которых можно будет потом сложить StackedBarChart.
        //Все null -> 10.0 (в целях тестирования)

        dataProperty.get().clear();
        for (int i = 0; i < maxLayers; i++) {
            dataProperty.get().add(new XYChart.Series<>());
        }

        for (int i = 0; i < maxLayers; i++) {
            dataProperty.get().set(i, CrossSectionConverters.getLayerOfPowers(picketObservableList, i));
            dataProperty.get().get(i).setName(((Integer) i).toString());
        }

    }

    protected Stage getStage() {
        return (Stage) sectionStackedBarChart.getScene().getWindow();
    }
}
