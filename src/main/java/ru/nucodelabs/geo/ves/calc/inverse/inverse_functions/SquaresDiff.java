package ru.nucodelabs.geo.ves.calc.inverse.inverse_functions;

import java.util.List;
import java.util.function.BiFunction;

import static java.lang.StrictMath.sqrt;

public class SquaresDiff implements BiFunction<List<Double>, List<Double>, Double> {
    @Override
    public Double apply(List<Double> solvedResistance, List<Double> experimentalResistance) {
        double functionValue = 0;
        for (int i = 0; i < solvedResistance.size(); i++) {
            functionValue += Math.pow(solvedResistance.get(i) - experimentalResistance.get(i), 2);
        }
        return sqrt(functionValue) / solvedResistance.size();
    }
}
