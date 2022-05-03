package ru.nucodelabs.gem.view.charts.cross_section;

import javafx.scene.chart.XYChart;
import ru.nucodelabs.data.ves.Picket;

import java.util.ArrayList;
import java.util.List;

public class CrossSectionConverters {

    @SuppressWarnings("unchecked")
    public static List<XYChart.Series<Number, Number>> makeResistanceSeries(List<Picket> pickets, double currentCoordinate) {
        List<XYChart.Series<Number, Number>> picketSeries = new ArrayList<>();
        double picketWidth = 100.0;

        for (Picket picket : pickets) {
            for (int i = 0; i < picket.getModelData().size() * 2 + pickets.size(); i++) {
                picketSeries.add(new XYChart.Series<>());
                String seriesName = picket.getName() + " - " + i;
                picketSeries.get(i).setName(seriesName);
            }
        }

        int count = 0;

        for (Picket picket : pickets) {
            List<Double> height = picket.zOfLayers();
            int i = 0;
            for (Double hValue : height) {
                double[] sides = layerSides(hValue, picket.getModelData().get(i).getPower());

                XYChart.Data<Number, Number> leftPositiveLineDot = new XYChart.Data<>(
                        currentCoordinate,
                        sides[0]);
                XYChart.Data<Number, Number> rightPositiveLineDot = new XYChart.Data<>(
                        currentCoordinate + picketWidth,
                        sides[0]);

                XYChart.Data<Number, Number> leftNegativeLineDot = new XYChart.Data<>(
                        currentCoordinate,
                        sides[1]);
                XYChart.Data<Number, Number> rightNegativeLineDot = new XYChart.Data<>(
                        currentCoordinate + picketWidth,
                        sides[1]);

                picketSeries.get(count++).getData().addAll(
                        leftPositiveLineDot,
                        rightPositiveLineDot);
                picketSeries.get(count++).getData().addAll(
                        leftNegativeLineDot,
                        rightNegativeLineDot
                );
                i++;
            }

            XYChart.Data<Number, Number> leftShiftLineDot = new XYChart.Data<>(
                    currentCoordinate,
                    shiftLayerLine(picket));
            XYChart.Data<Number, Number> rightShiftLineDot = new XYChart.Data<>(
                    currentCoordinate + picketWidth,
                    shiftLayerLine(picket));

            picketSeries.get(count++).getData().addAll(
                    leftShiftLineDot,
                    rightShiftLineDot
            );
            currentCoordinate += picketWidth;
        }


        return picketSeries;
    }

    private static double[] layerSides(Double hValue, Double power) {
        double[] res = new double[2];
        if (hValue < 0) {
            if (hValue + power > 0) {
                res[0] = hValue + power;
            } else {
                res[0] = 0;
            }
            res[1] = hValue;
        } else {
            res[0] = hValue + power;
            res[1] = 0;
        }

        return res;
    }

    private static double shiftLayerLine(Picket picket) {
        if (Math.abs(picket.zOfLayers().get(0)) - picket.getModelData().get(0).getPower() > 0 &
                Math.abs(picket.zOfLayers().get(picket.zOfLayers().size())) - picket.getModelData().get(0).getPower() > 0) {
            return picket.getZ();
        } else {
            return 0;
        }
    }
}
/*
if (picket.zOfLayers().get(0) < 0) {
                return picket.getZ();
            } else {
                return -picket.getZ();
            }
 */