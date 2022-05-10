package ru.nucodelabs.gem.view.charts.cross_section;

import javafx.scene.chart.XYChart;
import ru.nucodelabs.data.ves.Picket;

import java.util.ArrayList;
import java.util.List;

public class CrossSectionConverters {

    @SuppressWarnings("unchecked")
    public static List<XYChart.Series<Number, Number>> makeSectionSeries(List<Picket> pickets) {
        List<XYChart.Series<Number, Number>> picketSeries = new ArrayList<>();
        double maxDepth = 0;

        for (Picket picket : pickets) {
            if (picket.getModelData().size() > 0) {
                for (int i = 0; i < (picket.getModelData().size() + pickets.size()) * 2; i++) {
                    picketSeries.add(new XYChart.Series<>());
                    String seriesName = picket.getName() + " - " + i;
                    picketSeries.get(i).setName(seriesName);
                }
            }
            if (picket.zOfLayers().size() > 0) {
                if (picket.zOfLayers().get(picket.zOfLayers().size() - 1) <= maxDepth) {
                    maxDepth = picket.zOfLayers().get(picket.zOfLayers().size() - 1);
                }
            }
        }

        double lowBorder = computeLowBorder(maxDepth);

        int count = 0;
        double leftCoordinate = 0;
        double rightCoordinate = 0;

        for (Picket picket : pickets) {
            if (picket.getModelData().size() > 0) {
                List<Double> height = picket.zOfLayers();
                int i = 0;
                rightCoordinate = leftCoordinate + 2 * picket.getOffsetX();
                for (Double hValue : height) {

                    double[] sides = layerSides(hValue, picket.getModelData().get(i).getPower());

                    //Layer positive part
                    XYChart.Data<Number, Number> leftPositiveLineDot = new XYChart.Data<>(
                            leftCoordinate,
                            sides[0]);
                    XYChart.Data<Number, Number> rightPositiveLineDot = new XYChart.Data<>(
                            rightCoordinate,
                            sides[0]);

                    //Layer negative part
                    XYChart.Data<Number, Number> leftNegativeLineDot = new XYChart.Data<>(
                            leftCoordinate,
                            sides[1]);
                    XYChart.Data<Number, Number> rightNegativeLineDot = new XYChart.Data<>(
                            rightCoordinate,
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

                //White shifting layer
                XYChart.Data<Number, Number> leftShiftLineDot = new XYChart.Data<>(
                        leftCoordinate,
                        shiftLayerLine(picket));
                XYChart.Data<Number, Number> rightShiftLineDot = new XYChart.Data<>(
                        rightCoordinate,
                        shiftLayerLine(picket));

                picketSeries.get(count++).getData().addAll(
                        leftShiftLineDot,
                        rightShiftLineDot);

                //Gray infinite resistance
                XYChart.Data<Number, Number> leftInfinityLineDot = new XYChart.Data<>(
                        leftCoordinate,
                        lowBorder);
                XYChart.Data<Number, Number> rightInfinityLineDot = new XYChart.Data<>(
                        rightCoordinate,
                        lowBorder);

                picketSeries.get(count++).getData().addAll(
                        leftInfinityLineDot,
                        rightInfinityLineDot);

            }

            leftCoordinate = rightCoordinate;
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
        if (picket.zOfLayers().get(0) + picket.getModelData().get(0).getPower() < 0) {
            return picket.getZ();
        } else {
            return 0;
        }
    }

    private static double computeLowBorder(double maxDepth) {
        double roundNum = Math.ceil(maxDepth);
        double floor100Mult = Math.signum(maxDepth) * Math.floor(Math.abs(roundNum / 100));
        double floor10Mult = Math.signum(maxDepth) * Math.floor(Math.abs((roundNum - floor100Mult * 100) / 10));

        if (Math.abs(roundNum) < 10) {
            return roundNum;
        } else if (Math.abs(roundNum) < 100) {
            return (floor10Mult + 1 * Math.signum(maxDepth)) * 10;
        } else {
            return (floor100Mult + 1 * Math.signum(maxDepth)) * 100;
        }
    }
}