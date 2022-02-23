package ru.nucodelabs.gem.view.usercontrols.vescurves;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import ru.nucodelabs.gem.view.usercontrols.VBUserControl;

public class VESCurves extends VBUserControl {
    @FXML
    private LineChart<Double, Double> lineChart;
    @FXML
    private NumberAxis lineChartXAxis;
    @FXML
    private NumberAxis lineChartYAxis;
    @FXML
    private Button minusBtn;
    @FXML
    private Button plusBtn;
    @FXML
    private Button leftBtn;
    @FXML
    private Button rightBtn;
    @FXML
    private Button upBtn;
    @FXML
    private Button downBtn;

    public LineChart<Double, Double> getLineChart() {
        return lineChart;
    }

    public NumberAxis getLineChartXAxis() {
        return lineChartXAxis;
    }

    public NumberAxis getLineChartYAxis() {
        return lineChartYAxis;
    }

    public BooleanProperty legendVisibleProperty() {
        return lineChart.legendVisibleProperty();
    }

    public boolean isLegendVisible() {
        return lineChart.isLegendVisible();
    }

    public void setLegendVisible(boolean value) {
        lineChart.setLegendVisible(value);
    }

    public ObjectProperty<ObservableList<XYChart.Series<Double, Double>>> dataProperty() {
        return lineChart.dataProperty();
    }

    public ObservableList<XYChart.Series<Double, Double>> getData() {
        return lineChart.getData();
    }

    public void setData(ObservableList<XYChart.Series<Double, Double>> value) {
        lineChart.setData(value);
    }

    public DoubleProperty lowerBoundXProperty() {
        return lineChartXAxis.lowerBoundProperty();
    }

    public double getLowerBoundX() {
        return lineChartXAxis.getLowerBound();
    }

    public void setLowerBoundX(double value) {
        lineChartXAxis.setLowerBound(value);
    }

    public DoubleProperty lowerBoundYProperty() {
        return lineChartYAxis.lowerBoundProperty();
    }

    public double getLowerBoundY() {
        return lineChartYAxis.getLowerBound();
    }

    public void setLowerBoundY(double value) {
        lineChartYAxis.setLowerBound(value);
    }

    public DoubleProperty upperBoundXProperty() {
        return lineChartXAxis.upperBoundProperty();
    }

    public double getUpperBoundX() {
        return lineChartXAxis.getUpperBound();
    }

    public void setUpperBoundX(double value) {
        lineChartXAxis.setUpperBound(value);
    }

    public DoubleProperty upperBoundYProperty() {
        return lineChartYAxis.upperBoundProperty();
    }

    public double getUpperBoundY() {
        return lineChartYAxis.getUpperBound();
    }

    public void setUpperBoundY(double value) {
        lineChartYAxis.setUpperBound(value);
    }
}
