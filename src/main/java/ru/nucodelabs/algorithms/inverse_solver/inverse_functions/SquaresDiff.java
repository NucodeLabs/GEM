package ru.nucodelabs.algorithms.inverse_solver.inverse_functions;

import java.util.List;
import java.util.function.BiFunction;

public class SquaresDiff implements BiFunction<List<Double>, List<Double>, Double> {
    @Override
    public Double apply(List<Double> solvedResistance, List<Double> experimentalResistance) {
        double functionValue = 0;
        for (int i = 0; i < solvedResistance.size(); i++) {
            functionValue += Math.pow(solvedResistance.get(i) - experimentalResistance.get(i), 2);
        }

        return functionValue;
    }
}
