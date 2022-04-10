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
        /*categoryAxis.getCategories().clear();
        categoryAxis.getCategories().setAll(
                FXCollections.observableArrayList(
                        CrossSectionConverters.makeCategories(picketObservableList)));*/

        List<XYChart.Series<String, Number>> seriesList = CrossSectionConverters.getLayersOfPower(picketObservableList);

        dataProperty.get().setAll(FXCollections.observableArrayList(seriesList));
    }

    protected Stage getStage() {
        return (Stage) sectionStackedBarChart.getScene().getWindow();
    }

    public int getPicketCount() {
        return picketCount;
    }
}
