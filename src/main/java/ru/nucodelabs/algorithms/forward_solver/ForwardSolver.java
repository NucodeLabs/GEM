package ru.nucodelabs.algorithms.forward_solver;


import ru.nucodelabs.data.ves.ExperimentalMeasurement;
import ru.nucodelabs.data.ves.ModelLayer;

import java.util.List;

public interface ForwardSolver {

    /**
     * Returns default ForwardSolver implementation
     *
     * @param experimentalData experimental data of picket
     * @param modelData        model data of picket
     * @return new instance
     */
    static ForwardSolver createDefaultForwardSolver(List<ExperimentalMeasurement> experimentalData, List<ModelLayer> modelData) {
        return createSonetForwardSolver(experimentalData, modelData);
    }

    /**
     * Returns Sonet implementation of ForwardSolver
     *
     * @param experimentalData experimental data of picket
     * @param modelData        model data of picket
     * @return new instance
     */
    static ForwardSolver createSonetForwardSolver(List<ExperimentalMeasurement> experimentalData, List<ModelLayer> modelData) {
        return new SonetForwardSolver(experimentalData, modelData);
    }

    /**
     * Get solved resistance_apparent for chart
     *
     * @return resistance apparent
     */
    List<Double> solve();
}
