package ru.nucodelabs.gem.view.usercontrols.misfitstacks;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.text.Text;
import ru.nucodelabs.mvvm.VBUserControl;

/**
 * Misfit Stacks line chart with test above.
 */
public class MisfitStacks extends VBUserControl {

    @FXML
    public Text text;
    @FXML
    public LineChart<Double, Double> lineChart;
    @FXML
    public NumberAxis lineChartXAxis;
    @FXML
    public NumberAxis lineChartYAxis;

    public MisfitStacks() {
        super();
    }

    public Text getText() {
        return text;
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
