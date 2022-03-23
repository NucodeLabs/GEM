package ru.nucodelabs.algorithms.forward_solver;

import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;

import java.util.List;

public interface ForwardSolver {

    /**
     * Returns Sonet implementation of ForwardSolver
     *
     * @param picket picket to take data from
     * @return new instance
     */
    static ForwardSolver createSonetForwardSolver(ExperimentalData experimentalData, ModelData modelData) {
        return new SonetForwardSolver(experimentalData, modelData);
    }

    /**
     * Get solved resistance_apparent for chart
     *
     * @return resistance apparent
     */
    List<Double> solve();
}
