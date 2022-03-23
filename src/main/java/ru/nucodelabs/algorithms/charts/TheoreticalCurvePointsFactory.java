package ru.nucodelabs.algorithms.charts;

import ru.nucodelabs.algorithms.forward_solver.ForwardSolver;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.log10;
import static java.lang.Math.max;

public final class TheoreticalCurvePointsFactory implements PointsFactory {
    private final ExperimentalData experimentalData;
    private final ModelData modelData;

    public TheoreticalCurvePointsFactory(ExperimentalData experimentalData, ModelData modelData) {
        this.experimentalData = experimentalData;
        this.modelData = modelData;
    }

    @Override
    public List<Point> points() {
        if (experimentalData.size() == 0 || modelData.size() == 0) {
            return new ArrayList<>();
        }

        ForwardSolver forwardSolver = ForwardSolver.createSonetForwardSolver(experimentalData, modelData);

        List<Double> solvedResistance = new ArrayList<>(forwardSolver.solve());

        List<Point> points = new ArrayList<>();
        for (int i = 0; i < experimentalData.size(); i++) {
            double dotX = experimentalData.ab_2().get(i);
            double dotY = max(
                    solvedResistance.get(i),
                    0
            );
            points.add(new Point(dotX, dotY));
        }

        return points;
    }

    @Override
    public List<Point> log10Points() {
        if (experimentalData.size() == 0 || modelData.size() == 0) {
            return new ArrayList<>();
        }

        ForwardSolver forwardSolver = ForwardSolver.createSonetForwardSolver(experimentalData, modelData);

        List<Double> solvedResistance = new ArrayList<>(forwardSolver.solve());

        List<Point> points = new ArrayList<>();
        for (int i = 0; i < experimentalData.size(); i++) {
            double dotX = log10(experimentalData.ab_2().get(i));
            double dotY = max(
                    log10(solvedResistance.get(i)),
                    0
            );
            points.add(new Point(dotX, dotY));
        }

        return points;
    }
}
