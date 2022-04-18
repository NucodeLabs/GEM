package ru.nucodelabs.gem.view.cross_section;

import javafx.scene.chart.XYChart;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.VesUtils;

import java.util.ArrayList;
import java.util.List;

public class CrossSectionConverters {

    public static List<XYChart.Series<Number, Number>> makeResistanceSeries(List<Picket> pickets, double currentCoordinate) {
        List<XYChart.Series<Number, Number>> picketSeries = new ArrayList<>();
        double picketWidth = 100.0;

        for (Picket picket : pickets) {
            for (int i = 0; i < picket.getModelData().size(); i++) {
                picketSeries.add(new XYChart.Series<>());
                String seriesName = picket.getName() + " - " + i;
                picketSeries.get(i).setName(seriesName);
            }
        }

        int count = 0;
        for (Picket picket : pickets) {
            var height = VesUtils.powersToHeights(picket.getModelData());
            for (Double hValue : height) {

                XYChart.Data<Number, Number> leftLineDot = new XYChart.Data<>(
                        currentCoordinate,
                        hValue);
                XYChart.Data<Number, Number> rightLineDot = new XYChart.Data<>(
                        currentCoordinate + picketWidth,
                        hValue);

                picketSeries.get(count++).getData().addAll(
                        leftLineDot,
                        rightLineDot
                );

            }
            currentCoordinate += picketWidth;
        }

        return picketSeries;
    }

    public static int getMaxLayerCount(List<Picket> pickets) {
        int maxLayer = 0;
        for (Picket picket : pickets) {
            if (picket.getModelData() != null) {
                if (maxLayer < picket.getModelData().size()) {
                    maxLayer = picket.getModelData().size();
                }
            }
        }

        return maxLayer;
    }

    public static List<String> makeCategories(List<Picket> pickets) {
        List<String> categories = new ArrayList<>();
        int layers = getMaxLayerCount(pickets);

        for (int i = 0; i < layers; i++) {
            categories.add(((Integer) i).toString());
        }

        return categories;
    }
}
