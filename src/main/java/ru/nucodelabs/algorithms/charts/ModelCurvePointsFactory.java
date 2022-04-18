package ru.nucodelabs.algorithms.charts;

import ru.nucodelabs.data.ves.ModelData;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.log10;

final class ModelCurvePointsFactory implements PointsFactory {

    private static final double FIRST_X_DEFAULT = 1e-3;
    private static final double LAST_X_DEFAULT = 1e100;

    private final ModelData modelData;
    private final double FIRST_X;
    private final double LAST_X;

    ModelCurvePointsFactory(ModelData modelData) {
        this(modelData, FIRST_X_DEFAULT, LAST_X_DEFAULT);
    }

    ModelCurvePointsFactory(ModelData modelData, double firstX, double lastX) {
        this.modelData = modelData;
        FIRST_X = firstX;
        LAST_X = lastX;
    }

    @Override
    public List<Point> points() {
        if (modelData.size() == 0) {
            return new ArrayList<>();
        }

        List<Point> points = new ArrayList<>();
        // first point
        points.add(
                new Point(
                        FIRST_X,
                        modelData.resistance().get(0)
                )
        );

        Double prevSum = 0d;
        for (int i = 0; i < modelData.resistance().size() - 1; i++) {
            final Double currentResistance = modelData.resistance().get(i);
            final Double currentPower = modelData.power().get(i);

            points.add(
                    new Point(
                            currentPower + prevSum,
                            currentResistance
                    )
            );

            Double nextResistance = modelData.resistance().get(i + 1);
            points.add(
                    new Point(
                            currentPower + prevSum,
                            nextResistance
                    )
            );
            prevSum += currentPower;
        }

        // last point
        final int lastResistanceIndex = modelData.resistance().size() - 1;
        points.add(
                new Point(
                        LAST_X,
                        modelData.resistance().get(lastResistanceIndex)
                )
        );

        return points;
    }

    @Override
    public List<Point> log10Points() {
        if (modelData.size() == 0) {
            return new ArrayList<>();
        }

        List<Point> points = new ArrayList<>();
        // first point
        points.add(
                new Point(
                        log10(FIRST_X),
                        log10(modelData.resistance().get(0))
                )
        );

        Double prevSum = 0d;
        for (int i = 0; i < modelData.resistance().size() - 1; i++) {
            final Double currentResistance = modelData.resistance().get(i);
            final Double currentPower = modelData.power().get(i);

            points.add(
                    new Point(
                            log10(currentPower + prevSum),
                            log10(currentResistance)
                    )
            );

            Double nextResistance = modelData.resistance().get(i + 1);
            points.add(
                    new Point(
                            log10(currentPower + prevSum),
                            log10(nextResistance)
                    )
            );
            prevSum += currentPower;
        }

        // last point
        final int lastResistanceIndex = modelData.resistance().size() - 1;
        points.add(
                new Point(
                        log10(LAST_X),
                        log10(modelData.resistance().get(lastResistanceIndex))
                )
        );

        return points;
    }
}
