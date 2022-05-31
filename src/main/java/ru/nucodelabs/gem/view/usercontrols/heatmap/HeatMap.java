package ru.nucodelabs.gem.view.usercontrols.heatmap;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import ru.nucodelabs.gem.view.usercontrols.VBUserControl;

public class HeatMap extends VBUserControl {

    private final ObjectProperty<ObservableList<XYChart.Data<Double, Double>>> data
            = new SimpleObjectProperty<>(FXCollections.observableArrayList());

    @FXML
    private Canvas canvas;
    @FXML
    private VBox container;

    public ObservableList<XYChart.Data<Double, Double>> getData() {
        return data.get();
    }

    public void setData(ObservableList<XYChart.Data<Double, Double>> data) {
        this.data.set(data);
    }

    public ObjectProperty<ObservableList<XYChart.Data<Double, Double>>> dataProperty() {
        return data;
    }
}
