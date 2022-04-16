package ru.nucodelabs.algorithms.charts;

import ru.nucodelabs.algorithms.forward_solver.ForwardSolver;
import ru.nucodelabs.data.ves.ExperimentalMeasurement;
import ru.nucodelabs.data.ves.ModelLayer;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;

final class TheoreticalCurvePointsConverter implements PointsConverter {
    private final List<ExperimentalMeasurement> experimentalData;
    private final List<ModelLayer> modelData;

    public TheoreticalCurvePointsConverter(List<ExperimentalMeasurement> experimentalData, List<ModelLayer> modelData) {
        this.experimentalData = experimentalData;
        this.modelData = modelData;
    }

    @Override
    public List<Point> points() {
        if (experimentalData.size() == 0 || modelData.size() == 0) {
            return new ArrayList<>();
        }

        ForwardSolver forwardSolver = ForwardSolver.createDefaultForwardSolver(experimentalData, modelData);

        List<Double> solvedResistance = new ArrayList<>(forwardSolver.solve());

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
}
