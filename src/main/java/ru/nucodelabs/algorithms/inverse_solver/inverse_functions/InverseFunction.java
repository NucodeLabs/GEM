package ru.nucodelabs.algorithms.inverse_solver.inverse_functions;

import java.util.List;

public interface InverseFunction {
    double functionValue = 0;

    default double getValue(List<Double> solvedResistance, List<Double> experimentalResistance) {
        return functionValue;
    }
}
