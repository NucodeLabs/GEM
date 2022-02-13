package ru.nucodelabs.gem.view.usercontrols.misfitstacks;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import ru.nucodelabs.mvvm.VBUserControl;

public class MisfitStacks extends VBUserControl {

    @FXML
    private Label label;
    @FXML
    private LineChart<Double, Double> lineChart;
    @FXML
    private NumberAxis lineChartXAxis;
    @FXML
    private NumberAxis lineChartYAxis;

    public Label getLabel() {
        return label;
    }

    public LineChart<Double, Double> getLineChart() {
        return lineChart;
    }

    public NumberAxis getLineChartXAxis() {
        return lineChartXAxis;
    }

    public NumberAxis getLineChartYAxis() {
        return lineChartYAxis;
    }
}
