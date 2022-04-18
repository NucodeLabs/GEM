package ru.nucodelabs.gem.view.cross_section;

import javafx.scene.chart.XYChart;
import ru.nucodelabs.data.ves.Picket;

import java.util.ArrayList;
import java.util.List;

public class CrossSectionConverters {

    public static List<XYChart.Series<Number, Number>> makeResistanceSeries(List<Picket> pickets, double currentCoordinate) {
        List<XYChart.Series<Number, Number>> picketSeries = new ArrayList<>();
        double picketWidth = 100.0;

        for (Picket picket : pickets) {
            for (int i = 0; i < picket.modelData().getRows().size(); i++) {
                picketSeries.add(new XYChart.Series<>());
                String seriesName = picket.name() + " - " + i;
                picketSeries.get(i).setName(seriesName);
            }
        }

        int count = 0;
        for (Picket picket : pickets) {
            for (int i = 0; i < picket.modelData().getRows().size(); i++) {

                XYChart.Data<Number, Number> leftLineDot = new XYChart.Data<>(
                        currentCoordinate,
                        picket.modelData().getHeight().get(i));
                XYChart.Data<Number, Number> rightLineDot = new XYChart.Data<>(
                        currentCoordinate + picketWidth,
                        picket.modelData().getHeight().get(i));

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
            if (picket.modelData() != null) {
                if (maxLayer < picket.modelData().getRows().size()) {
                    maxLayer = picket.modelData().getRows().size();
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
