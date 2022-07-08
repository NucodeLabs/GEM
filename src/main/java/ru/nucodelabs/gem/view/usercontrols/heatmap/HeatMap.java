package ru.nucodelabs.gem.view.usercontrols.heatmap;

import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import ru.nucodelabs.algorithms.interpolation.PseudoInterpolator;
import ru.nucodelabs.gem.view.color.ColorMapper;
import ru.nucodelabs.gem.view.usercontrols.VBUserControl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static javafx.beans.binding.Bindings.when;

public class HeatMap extends VBUserControl {

    public HeatMap(@NamedArg("xAxis") NumberAxis xAxis, @NamedArg("yAxis") NumberAxis yAxis) {
        setXAxis(xAxis);
        setYAxis(yAxis);

        pane.getChildren().addAll(xAxis, yAxis);
        yAxis.setTickLabelFormatter(new AbsDecorator(yAxis.getTickLabelFormatter()));
        yAxis.tickLabelFormatterProperty().addListener((observable1, oldValue1, newValue1) -> {
            yAxis.setTickLabelFormatter(new AbsDecorator(newValue1));
        });

        setupAxes();
        setupCanvas();

        xAxisProperty().addListener((observable, oldValue, newValue) -> {
            pane.getChildren().remove(oldValue);
            pane.getChildren().add(newValue);
            setupAxes();
            setupCanvas();
        });

        yAxisProperty().addListener(((observable, oldValue, newValue) -> {
            pane.getChildren().remove(oldValue);
            pane.getChildren().add(newValue);
            newValue.setTickLabelFormatter(new AbsDecorator(newValue.getTickLabelFormatter()));
            newValue.tickLabelFormatterProperty().addListener((observable1, oldValue1, newValue1) -> {
                newValue.setTickLabelFormatter(new AbsDecorator(newValue1));
            });
            setupAxes();
            setupCanvas();
        }));

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
                newValue.blocksCountProperty().addListener((observable1, oldValue1, newValue1) -> repaint());
            }
        });
    }

    @FXML
    private Canvas canvas;
    @FXML
    private VBox container;
    private final ObjectProperty<NumberAxis> xAxis = new SimpleObjectProperty<>();
    private final ObjectProperty<NumberAxis> yAxis = new SimpleObjectProperty<>();
    @FXML
    private Pane pane;

    private final ObjectProperty<ColorMapper> colorPalette = new SimpleObjectProperty<>();

    private void updateAxes() {
        getXAxis().setLowerBound(data.get().stream().mapToDouble(XYChart.Data::getXValue).min().orElse(0));
        getXAxis().setUpperBound(data.get().stream().mapToDouble(XYChart.Data::getXValue).max().orElse(100));

        getYAxis().setUpperBound(-data.get().stream().mapToDouble(XYChart.Data::getYValue).min().orElse(0));
        getYAxis().setLowerBound(-data.get().stream().mapToDouble(XYChart.Data::getYValue).max().orElse(100));
    }

    private void setupCanvas() {
        canvas.widthProperty().unbind();
        canvas.widthProperty().bind(
                pane.widthProperty().subtract(getYAxis().widthProperty())
        );

        canvas.heightProperty().unbind();
        canvas.heightProperty().bind(
                pane.heightProperty().subtract(getXAxis().heightProperty())
        );

        canvas.layoutYProperty().unbind();
        canvas.layoutYProperty().bind(
                when(getXAxis().sideProperty().isEqualTo(Side.TOP))
                        .then(getXAxis().heightProperty())
                        .otherwise(0)
        );

        canvas.layoutXProperty().unbind();
        canvas.layoutXProperty().bind(
                when(getYAxis().sideProperty().isEqualTo(Side.LEFT))
                        .then(getYAxis().widthProperty())
                        .otherwise(0)
        );
    }

    private void setupAxes() {
        getXAxis().setAutoRanging(false);
        getYAxis().setAutoRanging(false);

        getXAxis().prefWidthProperty().unbind();
        getXAxis().prefWidthProperty().bind(canvas.widthProperty());

        getXAxis().layoutYProperty().unbind();
        getXAxis().layoutYProperty().bind(
                when(getXAxis().sideProperty().isEqualTo(Side.BOTTOM))
                        .then(canvas.heightProperty())
                        .otherwise(0)
        );

        getXAxis().layoutXProperty().unbind();
        getXAxis().layoutXProperty().bind(
                when(getYAxis().sideProperty().isEqualTo(Side.LEFT))
                        .then(getYAxis().widthProperty())
                        .otherwise(0)
        );


        getYAxis().prefHeightProperty().unbind();
        getYAxis().prefHeightProperty().bind(canvas.heightProperty());

        getYAxis().layoutXProperty().unbind();
        getYAxis().layoutXProperty().bind(
                when(getYAxis().sideProperty().isEqualTo(Side.RIGHT))
                        .then(canvas.widthProperty())
                        .otherwise(0)
        );

        getYAxis().layoutYProperty().unbind();
        getYAxis().layoutYProperty().bind(
                when(getXAxis().sideProperty().isEqualTo(Side.TOP))
                        .then(getXAxis().heightProperty())
                        .otherwise(0)
        );


    }

    private void update() {
        repaint();
        updateAxes();
    }

    private void repaint() {
        if (data.get().isEmpty()) {
            fillWhite();
            return;
        }

        var mapped = data.get().stream()
                .collect(Collectors.groupingBy(XYChart.Data::getXValue));

        List<List<XYChart.Data<Double, Double>>> lists = mapped.values().stream()
                .sorted(Comparator.comparingDouble(list -> list.get(0).getXValue()))
                .collect(Collectors.toList());

        try {
//                lists.get(0).forEach(dt -> dt.setXValue(0d));
            lists.set(0, lists.get(0).stream().map(dt -> new XYChart.Data<>(0.0, dt.getYValue(), dt.getExtraValue())).toList());
            new PseudoInterpolator(lists, colorPalette.get()).paint(canvas);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void fillWhite() {
        canvas.getGraphicsContext2D().setFill(Color.WHITE);
        canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private static class AbsDecorator extends StringConverter<Number> {

        private final StringConverter<Number> stringConverter;

        public AbsDecorator(StringConverter<Number> stringConverter) {
            this.stringConverter = stringConverter;
        }

        @Override
        public String toString(Number object) {
            if (stringConverter != null) {
                return stringConverter.toString().replaceFirst("-", "");
            } else {
                return String.valueOf(object.doubleValue()).replaceFirst("-", "");
            }
        }

        @Override
        public Number fromString(String string) {
            return stringConverter.fromString(string);
        }
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

    public NumberAxis getXAxis() {
        return xAxis.get();
    }

    public void setXAxis(NumberAxis xAxis) {
        this.xAxis.set(xAxis);
    }

    public ObjectProperty<NumberAxis> xAxisProperty() {
        return xAxis;
    }

    public NumberAxis getYAxis() {
        return yAxis.get();
    }

    public void setYAxis(NumberAxis yAxis) {
        this.yAxis.set(yAxis);
    }

    public ObjectProperty<NumberAxis> yAxisProperty() {
        return yAxis;
    }
}
