package ru.nucodelabs.gem.view.usercontrols.vescurves;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import ru.nucodelabs.mvvm.VBUserControl;

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

    public Button getMinusBtn() {
        return minusBtn;
    }

    public Button getPlusBtn() {
        return plusBtn;
    }

    public Button getLeftBtn() {
        return leftBtn;
    }

    public Button getRightBtn() {
        return rightBtn;
    }

    public Button getUpBtn() {
        return upBtn;
    }

    public Button getDownBtn() {
        return downBtn;
    }
}
