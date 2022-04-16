package ru.nucodelabs.algorithms.charts;

import ru.nucodelabs.data.ves.ModelLayer;

import java.util.ArrayList;
import java.util.List;

final class ModelCurvePointsConverter implements PointsConverter {

    private static final double FIRST_X_DEFAULT = 1e-2;
    private static final double LAST_X_DEFAULT = 1e100;

    private final List<ModelLayer> modelData;
    private final double FIRST_X;
    private final double LAST_X;

    ModelCurvePointsConverter(List<ModelLayer> modelData) {
        this(modelData, FIRST_X_DEFAULT, LAST_X_DEFAULT);
    }

    ModelCurvePointsConverter(List<ModelLayer> modelData, double firstX, double lastX) {
        this.modelData = modelData;
        FIRST_X = firstX;
        LAST_X = lastX;
    }

    @Override
    public List<Point> points() {
        if (modelData.size() == 0) {
            return new ArrayList<>();
        }

        List<Double> resistance = modelData.stream().map(ModelLayer::getResistance).toList();
        List<Double> power = modelData.stream().map(ModelLayer::getPower).toList();

        List<Point> points = new ArrayList<>();
        // first point
        points.add(
                new Point(
                        FIRST_X,
                        resistance.get(0)
                )
        );

        Double prevSum = 0d;
        for (int i = 0; i < modelData.size() - 1; i++) {
            final Double currentResistance = resistance.get(i);
            final Double currentPower = power.get(i);

            points.add(
                    new Point(
                            currentPower + prevSum,
                            currentResistance
                    )
            );

            Double nextResistance = resistance.get(i + 1);
            points.add(
                    new Point(
                            currentPower + prevSum,
                            nextResistance
                    )
            );
            prevSum += currentPower;
        }

        // last point
        final int lastResistanceIndex = resistance.size() - 1;
        points.add(
                new Point(
                        LAST_X,
                        resistance.get(lastResistanceIndex)
                )
        );

        return points;
    }
}
