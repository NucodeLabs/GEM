package ru.nucodelabs.gem.view.cross_section;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
        categoryAxis.getCategories().setAll(
                FXCollections.observableArrayList(
                        CrossSectionConverters.makeCategories(picketObservableList)));


        List<XYChart.Series<String, Number>> seriesList = CrossSectionConverters.getLayersOfPower(picketObservableList);

        dataProperty.get().setAll(FXCollections.observableArrayList(seriesList));
        updateBarColor(dataProperty.get());

    }

    private void updateBarColor(List<XYChart.Series<String, Number>> seriesList) {
        int layer = 0;
        int picket = 0;
        for (XYChart.Series<String, Number> series : seriesList) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                double resistance = picketObservableList.get(picket).modelData().resistance().get(layer);
                Node node = data.getNode();
                //if (node != null) {
                    if (resistance <= 10) {
                        node.setStyle("-fx-bar-fill: #74ee6d");
                    } else if (10 < resistance && resistance <= 30) {
                        node.setStyle("-fx-bar-fill: #019301");
                    } else if (30 < resistance && resistance <= 60) {
                        node.setStyle("-fx-bar-fill: #d0ff00");
                    } else if (60 < resistance && resistance <= 120) {
                        node.setStyle("-fx-bar-fill: #f8ff00");
                    } else if (120 < resistance && resistance <= 200) {
                        node.setStyle("-fx-bar-fill: #ffd700");
                    } else if (200 < resistance && resistance <= 300) {
                        node.setStyle("-fx-bar-fill: #ff9100");
                    } else if (300 < resistance && resistance <= 500) {
                        node.setStyle("-fx-bar-fill: #ff6200");
                    } else if (500 < resistance && resistance <= 1000) {
                        node.setStyle("-fx-bar-fill: #ff4d00");
                    } else if (1000 < resistance) {
                        node.setStyle("-fx-bar-fill: #ff0000");
                    }
                    if (resistance == 0) {
                        node.setStyle("-fx-bar-fill: #ffffff");
                    }
                //}

                picket++;
            }
            picket %= picketObservableList.size();
            layer++;
        }
    }

    protected Stage getStage() {
        return (Stage) sectionStackedBarChart.getScene().getWindow();
    }

    public int getPicketCount() {
        return picketCount;
    }
}
