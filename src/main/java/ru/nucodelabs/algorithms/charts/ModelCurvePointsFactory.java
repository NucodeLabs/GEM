package ru.nucodelabs.algorithms.charts;

import ru.nucodelabs.data.ves.ModelData;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.log10;

public final class ModelCurvePointsFactory implements PointsFactory {

    private final ModelData modelData;
    private double FIRST_X = 1e-2;
    private double LAST_X = 1e100;

    ModelCurvePointsFactory(ModelData modelData) {
        this.modelData = modelData;
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
