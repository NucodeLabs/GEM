package ru.nucodelabs.gem.view.charts.cross_section;

import javafx.scene.chart.XYChart;
import ru.nucodelabs.data.ves.Picket;

import java.util.ArrayList;
import java.util.List;

public class CrossSectionConverters {

    /**
     * Computes series for each layer of all pickets of the section, which are intended to be shown as lines.
     * Designed to be used in AreaChart. Because of this it computes positive and negative lines for each layer of all pickets.
     * In addition, there are lines for bottom infinite layer and white (blank) for shifting layer.
     * @param pickets pickets of the section
     * @return series for drawing
     */
    @SuppressWarnings("unchecked")
    public static List<XYChart.Series<Number, Number>> makeSectionSeries(List<Picket> pickets) {
        List<XYChart.Series<Number, Number>> picketSeries = new ArrayList<>();
        double maxDepth = 0;

        //Create empty named series and find the depth of the deepest picket of the section
        for (Picket picket : pickets) {
            if (picket.getModelData().size() > 0) {
                //There positive and negative for each layer +
                //      + Z shift white layer and bottom infinite resistance layer for each picket
                //That's why (picket.getModelData().size() + pickets.size()) * 2
                for (int i = 0; i < (picket.getModelData().size() + pickets.size()) * 2; i++) {
                    picketSeries.add(new XYChart.Series<>());
                    String seriesName = picket.getName() + " - " + i;
                    picketSeries.get(i).setName(seriesName);
                }
            }
            if (picket.zOfModelLayers().size() > 0) {
                if (picket.zOfModelLayers().get(picket.zOfModelLayers().size() - 1) <= maxDepth) {
                    maxDepth = picket.zOfModelLayers().get(picket.zOfModelLayers().size() - 1);
                }
            }
        }

        //New rounded depth closest to maxDepth
        double lowBorder = computeLowBorder(maxDepth);

        //Important counter for series list
        int count = 0;

        //Picket center coordinate
        double centerCoordinate = 0;
        //Picket left coordinate
        double leftCoordinate;
        //Picket right coordinate
        double rightCoordinate;

        for (Picket picket : pickets) {
            if (picket.getModelData().size() > 0) {
                List<Double> height = picket.zOfModelLayers();
                int i = 0;

                //If there is only one picket, then its width is AB (AB/2 in both sides of center)
                if (pickets.size() == 1) {
                    if (picket.getExperimentalData().size() > 0) {
                        leftCoordinate = centerCoordinate - 0.5 * picket.getExperimentalData().get(picket.getExperimentalData().size() - 1).getAb2();
                        rightCoordinate = centerCoordinate + 0.5 * picket.getExperimentalData().get(picket.getExperimentalData().size() - 1).getAb2();
                    } else {
                        //If there is picket with no experimental data. Causes SIGSEGV in forwardsolver, when pressing inverse task
                        centerCoordinate = 0;
                        leftCoordinate = centerCoordinate - 0.5 * picket.getOffsetX();
                        rightCoordinate = centerCoordinate + 0.5 * picket.getOffsetX();
                    }
                } else {
                    //Auto setting of center coordinate of the first picket to zero
                    if (pickets.indexOf(picket) == 0) {
                        centerCoordinate = 0;
                        picket.withOffsetX(0);
                    } else {
                        //Picket center X shift by offset value
                        centerCoordinate += picket.getOffsetX();
                    }

                    leftCoordinate = centerCoordinate - 0.5 * picket.getOffsetX();

                    if (pickets.indexOf(picket) + 1 < pickets.size()) {
                        //If this picket is not last, then
                        //width to right equals to the half of the offset of the next picket
                        rightCoordinate = centerCoordinate + 0.5 * pickets.get(pickets.indexOf(picket) + 1).getOffsetX();
                    } else {
                        rightCoordinate = centerCoordinate + 0.5 * picket.getOffsetX();
                    }
                }
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

                    //Add to series
                    picketSeries.get(count++).getData().addAll(
                            leftPositiveLineDot,
                            rightPositiveLineDot);
                    picketSeries.get(count++).getData().addAll(
                            leftNegativeLineDot,
                            rightNegativeLineDot
                    );
                    i++;
                }

                //White (blank) shifting layer
                XYChart.Data<Number, Number> leftShiftLineDot = new XYChart.Data<>(
                        leftCoordinate,
                        shiftLayerLine(picket));
                XYChart.Data<Number, Number> rightShiftLineDot = new XYChart.Data<>(
                        rightCoordinate,
                        shiftLayerLine(picket));

                //Add to series
                picketSeries.get(count++).getData().addAll(
                        leftShiftLineDot,
                        rightShiftLineDot);

                //Infinite resistance layer
                XYChart.Data<Number, Number> leftInfinityLineDot = new XYChart.Data<>(
                        leftCoordinate,
                        lowBorder);
                XYChart.Data<Number, Number> rightInfinityLineDot = new XYChart.Data<>(
                        rightCoordinate,
                        lowBorder);

                //Add to series
                picketSeries.get(count++).getData().addAll(
                        leftInfinityLineDot,
                        rightInfinityLineDot);

            }
        }

        return picketSeries;
    }

    /**
     * According to depth(Z) and power of current layer computes values of positive and negative parts.
     * @param hValue depth of layer (Z)
     * @param power power of layer
     * @return res[0] - value for positive part, res[1] - value for negative part.
     */
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

    /**
     * Compute shift value. 0 - if picket is above X axis. Z of picket, if its below X axis.
     * @param picket current picket
     * @return power
     */
    private static double shiftLayerLine(Picket picket) {
        if (picket.zOfModelLayers().get(0) + picket.getModelData().get(0).getPower() < 0) {
            return picket.getZ();
        } else {
            return 0;
        }
    }

    /**
     * Computes rounded low border for all pickets.
     * @param maxDepth current maxDepth
     * @return upper rounded maxDepth, if maxDepth <= 10;
     *  maxDepth rounded to next double % 10 == 0, if maxDepth <= 100;
     *  maxDepth rounded to next double % 100 == 0, in other cases
     */
    private static double computeLowBorder(double maxDepth) {
        double roundNum = Math.ceil(maxDepth);
        double floor100Mult = Math.signum(maxDepth) * Math.floor(Math.abs(roundNum / 100));
        double floor10Mult = Math.signum(maxDepth) * Math.floor(Math.abs((roundNum - floor100Mult * 100) / 10));

        if (Math.abs(roundNum) <= 10) {
            return roundNum;
        } else if (Math.abs(roundNum) <= 100) {
            return (floor10Mult + 1 * Math.signum(maxDepth)) * 10;
        } else {
            return (floor100Mult + 1 * Math.signum(maxDepth)) * 100;
        }
    }
}