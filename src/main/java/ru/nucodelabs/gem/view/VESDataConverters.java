package ru.nucodelabs.gem.view;

import javafx.scene.chart.XYChart;
import ru.nucodelabs.data.ves.ModelData;

public class VESDataConverters {
    public ModelData toModelData(XYChart.Series<Double, Double> modelCurveSeries) {
        return new ModelData();
    }
}
