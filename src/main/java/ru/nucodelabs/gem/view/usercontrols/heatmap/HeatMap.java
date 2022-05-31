package ru.nucodelabs.gem.view.usercontrols.heatmap;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import ru.nucodelabs.algorithms.interpolation.PseudoInterpolator;
import ru.nucodelabs.gem.view.usercontrols.VBUserControl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HeatMap extends VBUserControl {

    public HeatMap() {
        canvas.widthProperty().addListener((observable, oldValue, newValue) -> repaint());
        canvas.heightProperty().addListener(((observable, oldValue, newValue) -> repaint()));

        data.get().addListener((ListChangeListener<? super XYChart.Data<Double, Double>>) c -> repaint());
        data.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.addListener(
                        (ListChangeListener<? super XYChart.Data<Double, Double>>) c -> repaint());
            }
            repaint();
        });
    }

    private void repaint() {
        var mapped = data.get().stream()
                .collect(Collectors.groupingBy(XYChart.Data::getXValue));

        List<List<XYChart.Data<Double, Double>>> lists = new ArrayList<>(mapped.values().stream()
                .sorted(Comparator.comparingDouble(list -> list.get(0).getXValue()))
                .toList());

        if (!lists.isEmpty()) {
            try {
                new PseudoInterpolator(lists).paint(canvas);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            canvas.getGraphicsContext2D().setFill(Color.WHITE);
            canvas.getGraphicsContext2D().fill();
        }
    }

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
