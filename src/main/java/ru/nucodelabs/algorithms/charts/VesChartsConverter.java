package ru.nucodelabs.algorithms.charts;


import ru.nucodelabs.algorithms.forward_solver.ForwardSolver;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelLayer;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;

public class VesChartsConverter {

    private static final VesChartsConverter INSTANCE = new VesChartsConverter();

    private VesChartsConverter() {
    }

    public static VesChartsConverter getInstance() {
        return INSTANCE;
    }

    public List<Point> experimentalCurveOf(List<ExperimentalData> experimentalData) {
        if (experimentalData.isEmpty()) {
            return new ArrayList<>();
        }

        List<Point> points = new ArrayList<>();

        for (ExperimentalData experimentalDatum : experimentalData) {
            double dotX = experimentalDatum.getAb2();
            double dotY = Math.max(experimentalDatum.getResistanceApparent(), 0);

            points.add(new Point(dotX, dotY));
        }

        return points;
    }


    public List<Point> experimentalCurveErrorBoundOf(List<ExperimentalData> experimentalData, BoundType boundType) {
        if (experimentalData.isEmpty()) {
            return new ArrayList<>();
        }

        List<Point> points = new ArrayList<>();

        for (ExperimentalData experimentalDatum : experimentalData) {
            double dotX = experimentalDatum.getAb2();
            double error = experimentalDatum.getErrorResistanceApparent() / 100f;
            double dotY;
            if (boundType == BoundType.UPPER_BOUND) {
                dotY = Math.max(
                        experimentalDatum.getResistanceApparent()
                                + experimentalDatum.getResistanceApparent() * error,
                        0);
            } else {
                dotY = Math.max(
                        experimentalDatum.getResistanceApparent()
                                - experimentalDatum.getResistanceApparent() * error,
                        0);
            }

            points.add(new Point(dotX, dotY));
        }

        return points;
    }

    public List<Point> theoreticalCurveOf(List<ExperimentalData> experimentalData, List<ModelLayer> modelData) {
        if (experimentalData.isEmpty() || modelData.isEmpty()) {
            return new ArrayList<>();
        }

        ForwardSolver forwardSolver = ForwardSolver.getDefaultImpl();

        List<Double> solvedResistance = new ArrayList<>(forwardSolver.solve(experimentalData, modelData));

        List<Point> points = new ArrayList<>();
        for (int i = 0; i < experimentalData.size(); i++) {
            double dotX = experimentalData.get(i).getAb2();
            double dotY = max(
                    solvedResistance.get(i),
                    0
            );
            points.add(new Point(dotX, dotY));
        }

        return points;
    }

    public List<Point> modelCurveOf(List<ModelLayer> modelData) {
        final double FIRST_X = 1e-2;
        final double LAST_X = 1e100;

        return modelCurveOf(modelData, FIRST_X, LAST_X);
    }

    public List<Point> modelCurveOf(List<ModelLayer> modelData, double firstX, double lastX) {

        if (modelData.isEmpty()) {
            return new ArrayList<>();
        }

        List<Point> points = new ArrayList<>();
        // first point
        points.add(
                new Point(
                        firstX,
                        modelData.get(0).getResistance()
                )
        );

        double prevSum = 0d;
        for (int i = 0; i < modelData.size() - 1; i++) {
            final double currentResistance = modelData.get(i).getResistance();
            final double currentPower = modelData.get(i).getPower();

            points.add(
                    new Point(
                            currentPower + prevSum,
                            currentResistance
                    )
            );

            double nextResistance = modelData.get(i + 1).getResistance();
            points.add(
                    new Point(
                            currentPower + prevSum,
                            nextResistance
                    )
            );
            prevSum += currentPower;
        }

        // last point
        final int lastResistanceIndex = modelData.size() - 1;
        points.add(
                new Point(
                        lastX,
                        modelData.get(lastResistanceIndex).getResistance()
                )
        );

        return points;
    }

    public enum BoundType {UPPER_BOUND, LOWER_BOUND}
}
