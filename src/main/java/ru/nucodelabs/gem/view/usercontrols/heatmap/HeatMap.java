package ru.nucodelabs.gem.view.usercontrols.heatmap;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import ru.nucodelabs.algorithms.interpolation.PseudoInterpolator;
import ru.nucodelabs.gem.view.color.ColorMapper;
import ru.nucodelabs.gem.view.usercontrols.VBUserControl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HeatMap extends VBUserControl {

    private final StringProperty xAxisLabel = new SimpleStringProperty("X");
    private final StringProperty yAxisLabel = new SimpleStringProperty("Y");
    @FXML
    private Canvas canvas;
    @FXML
    private VBox container;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private final ObjectProperty<ColorMapper> colorPalette = new SimpleObjectProperty<>();

    public HeatMap() {
        canvas.widthProperty().addListener((observable, oldValue, newValue) -> repaint());
        canvas.heightProperty().addListener(((observable, oldValue, newValue) -> repaint()));

        data.get().addListener((ListChangeListener<? super XYChart.Data<Double, Double>>) c -> update());
        data.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.addListener(
                        (ListChangeListener<? super XYChart.Data<Double, Double>>) c -> update());
            }
            update();
        });

        colorPalette.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                repaint();
                newValue.maxValueProperty().addListener((observable1, oldValue1, newValue1) -> repaint());
                newValue.minValueProperty().addListener((observable1, oldValue1, newValue1) -> repaint());
            }
        });

        xAxis.labelProperty().bind(xAxisLabel);
        yAxis.labelProperty().bind(yAxisLabel);
    }

    private void update() {
        repaint();
        updateAxes();
    }

    private void repaint() {
        var mapped = data.get().stream()
                .collect(Collectors.groupingBy(XYChart.Data::getXValue));

        List<List<XYChart.Data<Double, Double>>> lists = mapped.values().stream()
                .sorted(Comparator.comparingDouble(list -> list.get(0).getXValue()))
                .collect(Collectors.toList());

        if (!lists.isEmpty()) {
            try {
                lists.get(0).forEach(dt -> dt.setXValue(0d));
                new PseudoInterpolator(lists, colorPalette.get()).paint(canvas);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            canvas.getGraphicsContext2D().setFill(Color.WHITE);
            canvas.getGraphicsContext2D().fill();
        }
    }

    private void updateAxes() {
        xAxis.setLowerBound(data.get().stream().mapToDouble(XYChart.Data::getXValue).min().orElse(0));
        xAxis.setUpperBound(data.get().stream().mapToDouble(XYChart.Data::getXValue).max().orElse(100));

        yAxis.setLowerBound(data.get().stream().mapToDouble(XYChart.Data::getYValue).min().orElse(0));
        yAxis.setUpperBound(data.get().stream().mapToDouble(XYChart.Data::getYValue).max().orElse(100));
    }

    private final ObjectProperty<ObservableList<XYChart.Data<Double, Double>>> data
            = new SimpleObjectProperty<>(FXCollections.observableArrayList());

    public ObservableList<XYChart.Data<Double, Double>> getData() {
        return data.get();
    }

    public void setData(ObservableList<XYChart.Data<Double, Double>> data) {
        this.data.set(data);
    }

    public ObjectProperty<ObservableList<XYChart.Data<Double, Double>>> dataProperty() {
        return data;
    }

    public ColorMapper getColorPalette() {
        return colorPalette.get();
    }

    public void setColorPalette(ColorMapper colorPalette) {
        this.colorPalette.set(colorPalette);
    }

    public ObjectProperty<ColorMapper> colorPaletteProperty() {
        return colorPalette;
    }

    public String getXAxisLabel() {
        return xAxisLabel.get();
    }

    public void setXAxisLabel(String xAxisLabel) {
        this.xAxisLabel.set(xAxisLabel);
    }

    public StringProperty xAxisLabelProperty() {
        return xAxisLabel;
    }

    public String getYAxisLabel() {
        return yAxisLabel.get();
    }

    public void setYAxisLabel(String yAxisLabel) {
        this.yAxisLabel.set(yAxisLabel);
    }

    public StringProperty yAxisLabelProperty() {
        return yAxisLabel;
    }
}
