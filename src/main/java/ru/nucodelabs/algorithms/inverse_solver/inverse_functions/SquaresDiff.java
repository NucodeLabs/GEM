package ru.nucodelabs.algorithms.inverse_solver.inverse_functions;

import java.util.List;

public class SquaresDiff implements InverseFunction {
    @Override
    public double getValue(List<Double> solvedResistance, List<Double> experimentalResistance) {
        double functionValue = 0;
        for (int i = 0; i < solvedResistance.size(); i++) {
            functionValue += Math.abs(Math.pow(solvedResistance.get(i), 2) - Math.pow(experimentalResistance.get(i), 2));
        }

        return functionValue;
    }
}
