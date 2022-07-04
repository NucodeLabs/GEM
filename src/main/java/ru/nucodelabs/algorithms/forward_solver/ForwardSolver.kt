package ru.nucodelabs.algorithms.forward_solver;


import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelLayer;

import java.util.List;

public interface ForwardSolver {

    ForwardSolver DEFAULT_IMPL = new SonetForwardSolver();

    static ForwardSolver getDefaultImpl() {
        return DEFAULT_IMPL;
    }

    /**
     * Returns default ForwardSolver implementation
     *
     * @return new instance
     */
    static ForwardSolver createDefaultForwardSolver() {
        return createSonetForwardSolver();
    }

    /**
     * Returns Sonet implementation of ForwardSolver
     *
     * @return new instance
     */
    static ForwardSolver createSonetForwardSolver() {
        return new SonetForwardSolver();
    }

    /**
     * Get solved resistance_apparent for chart
     *
     * @return resistance apparent
     */
    List<Double> solve(List<ExperimentalData> experimentalData, List<ModelLayer> modelData);
}
