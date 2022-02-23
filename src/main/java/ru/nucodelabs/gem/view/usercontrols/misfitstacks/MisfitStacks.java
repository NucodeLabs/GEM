package ru.nucodelabs.gem.view.usercontrols.misfitstacks;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import ru.nucodelabs.gem.view.usercontrols.VBUserControl;

public class MisfitStacks extends VBUserControl {

    @FXML
    private LineChart<Double, Double> lineChart;
    @FXML
    private NumberAxis lineChartXAxis;
    @FXML
    private NumberAxis lineChartYAxis;

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
}
