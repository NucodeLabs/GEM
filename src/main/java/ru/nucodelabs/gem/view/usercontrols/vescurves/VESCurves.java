package ru.nucodelabs.gem.view.usercontrols.vescurves;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import ru.nucodelabs.gem.view.PowerOf10Formatter;
import ru.nucodelabs.mvvm.VBUserControl;

public class VESCurves extends VBUserControl {
    @FXML
    private LineChart<Double, Double> lineChart;
    @FXML
    private NumberAxis lineChartXAxis;
    @FXML
    private NumberAxis lineChartYAxis;

    public VESCurves() {
        super();
        lineChartXAxis.setTickLabelFormatter(new PowerOf10Formatter());
        lineChartYAxis.setTickLabelFormatter(new PowerOf10Formatter());
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
