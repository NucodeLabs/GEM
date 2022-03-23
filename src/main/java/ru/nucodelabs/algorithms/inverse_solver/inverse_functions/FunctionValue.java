package ru.nucodelabs.algorithms.inverse_solver.inverse_functions;

import org.apache.commons.math3.analysis.MultivariateFunction;
import ru.nucodelabs.algorithms.ForwardSolver;
import ru.nucodelabs.data.ves.ExperimentalData;

import java.util.ArrayList;
import java.util.List;

public class FunctionValue implements MultivariateFunction {
    private final ExperimentalData experimentalData;
    private final InverseFunction inverseFunction;

    public FunctionValue(ExperimentalData experimentalData, InverseFunction inverseFunction) {
        this.experimentalData = experimentalData;
        this.inverseFunction = inverseFunction;
    }

    @Override
    public double value(double[] variables) {
        List<Double> currentModelResistance = new ArrayList<>();
        List<Double> currentModelPower = new ArrayList<>();

        for (int i = 0; i < (variables.length + 1) / 2; i++) {
            currentModelResistance.add(Math.exp(variables[i]));
        }
        for (int i = (variables.length + 1) / 2; i < variables.length; i++) {
            currentModelPower.add(Math.exp(variables[i]));
        }
        currentModelPower.add(0.0);

        List<Double> experimentalAB_2 = experimentalData.ab_2();
        List<Double> solvedResistance = ForwardSolver.ves(currentModelResistance, currentModelPower, experimentalAB_2);

        List<Double> experimentalResistance = experimentalData.resistanceApparent();

        return inverseFunction.getValue(solvedResistance, experimentalResistance);
    }
}
