package ru.nucodelabs.algorithms.inverse_solver.inverse_functions;

import org.apache.commons.math3.analysis.MultivariateFunction;
import ru.nucodelabs.algorithms.ForwardSolver;
import ru.nucodelabs.data.ves.ExperimentalData;

import java.util.ArrayList;
import java.util.List;

public class SquaresDiff implements MultivariateFunction {

    private final ExperimentalData experimentalData;

    public SquaresDiff(ExperimentalData experimentalData) {
        this.experimentalData = experimentalData;
    }

    @Override
    public double value(double[] variables) {
        double functionValue = 0;

        List<Double> currentModelPower = new ArrayList<>();
        List<Double> currentModelResistance = new ArrayList<>();

        for (int i = 0; i < variables.length - 1; i += 2) {
            currentModelPower.add(Math.exp(variables[i]));
            currentModelResistance.add(Math.exp(variables[i + 1]));
        }
        currentModelPower.add(0.0);
        currentModelResistance.add(Math.exp(variables[variables.length - 1]));

        List<Double> experimentalAB_2 = experimentalData.ab_2();
        List<Double> solvedResistance = ForwardSolver.ves(currentModelResistance, currentModelPower, experimentalAB_2);

        List<Double> experimentalResistance = experimentalData.resistanceApparent();
        for (int i = 0; i < solvedResistance.size(); i++) {
            functionValue += Math.abs(Math.pow(solvedResistance.get(i), 2) - Math.pow(experimentalResistance.get(i), 2));
        }

        return functionValue;
    }
}
