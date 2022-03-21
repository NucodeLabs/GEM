package ru.nucodelabs.gem.view.convert;

import javafx.scene.chart.XYChart;
import ru.nucodelabs.data.ves.Picket;

import java.util.ArrayList;
import java.util.List;

public class CrossSectionConverters {

    public static XYChart.Series<String, Number> getLayerOfPowers(List<Picket> pickets, int layerNum) {
        XYChart.Series<String, Number> layerOfLayerPowers = new XYChart.Series<>();
        List<Double> powerList = new ArrayList<>();

        if (pickets != null) {
            for (Picket picket : pickets) {
                if (picket.modelData() != null) {
                    if (layerNum < picket.modelData().getSize()) {
                        powerList.add(picket.modelData().power().get(layerNum));
                    }
                } else {
                    powerList.add(10.0);
                }
            }

            for (Double aDouble : powerList) {
                String layerNumStr = ((Integer) layerNum).toString();
                double layerPower = aDouble;

                XYChart.Data<String, Number> pair = new XYChart.Data<>(layerNumStr, layerPower);

                layerOfLayerPowers.getData().add(pair);
            }
        }

        return layerOfLayerPowers;
    }

    public static XYChart.Series<String, Number> changeSeriesType(XYChart.Series<Double, Double> series) {
        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();

        if (series != null) {
            for (XYChart.Data<Double, Double> data : series.getData()) {
                String xValue = data.getXValue().toString();
                Number yValue = data.getYValue();
                barSeries.getData().add(new XYChart.Data<>(xValue, yValue));
            }
        }

        return barSeries;
    }
}
