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

    private final ObservableList<Picket> picketObservableList;
    public int picketCount;
    @FXML
    public CategoryAxis categoryAxis;
    @FXML
    public NumberAxis numberAxis;
    @FXML
    public StackedBarChart<String, Number> sectionStackedBarChart;

    @Inject
    private ObjectProperty<ObservableList<XYChart.Series<String, Number>>> dataProperty;

    @Inject
    public CrossSectionController(ObservableList<Picket> picketObservableList) {
        this.picketObservableList = picketObservableList;

        this.picketObservableList.addListener((ListChangeListener<Picket>) c -> {
            if (c.next()) {
                update();
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //uiProperties = resources;
        sectionStackedBarChart.dataProperty().bind(dataProperty);

    }

    public void update() {
        List<XYChart.Series<String, Number>> seriesList = new ArrayList<>();

        categoryAxis.getCategories().addAll(
                FXCollections.observableArrayList(
                        CrossSectionConverters.makeCategories(picketObservableList)));

        XYChart.Series<String, Number> dataSeries1 = new XYChart.Series<String, Number>();
        dataSeries1.setName("0");

        dataSeries1.getData().add(new XYChart.Data<String, Number>("0", 567));
        dataSeries1.getData().add(new XYChart.Data<String, Number>("1", 540));

        sectionStackedBarChart.getData().add(dataSeries1);

        XYChart.Series<String, Number> dataSeries2 = new XYChart.Series<>();
        dataSeries2.setName("1");

        dataSeries2.getData().add(new XYChart.Data<>("2", 65));
        dataSeries2.getData().add(new XYChart.Data<>("3", 120));

        sectionStackedBarChart.getData().add(dataSeries2);

        XYChart.Series<String, Number> dataSeries3 = new XYChart.Series<>();
        dataSeries3.setName("2");

        dataSeries3.getData().add(new XYChart.Data<>("4", 23));
        dataSeries3.getData().add(new XYChart.Data<>("5", 36));

        sectionStackedBarChart.getData().add(dataSeries3);
        /*categoryAxis.getCategories().addAll(
                FXCollections.observableArrayList(
                        CrossSectionConverters.makeCategories(picketObservableList)));*/

        /*if (picketObservableList != null) {
            seriesList = CrossSectionConverters.getLayersOfPowers(picketObservableList);
        }
        dataProperty.get().clear();
        dataProperty.get().addAll(seriesList);
        //dataProperty.set(FXCollections.observableArrayList(seriesList));*/
    }

    protected Stage getStage() {
        return (Stage) sectionStackedBarChart.getScene().getWindow();
    }

    public int getPicketCount() {
        return picketCount;
    }
}
