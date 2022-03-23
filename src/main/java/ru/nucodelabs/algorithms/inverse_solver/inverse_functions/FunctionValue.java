package ru.nucodelabs.algorithms.inverse_solver.inverse_functions;

import org.apache.commons.math3.analysis.MultivariateFunction;
import ru.nucodelabs.algorithms.forward_solver.ForwardSolver;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class FunctionValue implements MultivariateFunction {
    private final ExperimentalData experimentalData;
    private final BiFunction<List<Double>, List<Double>, Double> inverseFunction;

    public FunctionValue(ExperimentalData experimentalData, BiFunction<List<Double>, List<Double>, Double> inverseFunction) {
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

        List<Double> solvedResistance = ForwardSolver.createSonetForwardSolver(
                experimentalData,
                new ModelData(currentModelResistance, new ArrayList<>(), currentModelPower)
        ).solve();

        List<Double> experimentalResistance = experimentalData.resistanceApparent();

        return inverseFunction.apply(solvedResistance, experimentalResistance);
    }
}
