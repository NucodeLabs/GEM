package ru.nucodelabs.gem.view.charts.cross_section;

import javafx.scene.chart.XYChart;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.data.ves.VesUtils;

import java.util.ArrayList;
import java.util.List;

public class CrossSectionConverters {

    @SuppressWarnings("unchecked")
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

                // safe
                picketSeries.get(count++).getData().addAll(
                        leftLineDot,
                        rightLineDot
                );

            }
            currentCoordinate += picketWidth;
        }

        return picketSeries;
    }
}
