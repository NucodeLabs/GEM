package ru.nucodelabs.algorithms.inverse_solver.inverse_functions;

import org.apache.commons.math3.analysis.MultivariateFunction;
import ru.nucodelabs.algorithms.forward_solver.ForwardSolver;
import ru.nucodelabs.data.ves.ExperimentalMeasurement;
import ru.nucodelabs.data.ves.ModelLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class FunctionValue implements MultivariateFunction {
    private final List<ExperimentalMeasurement> experimentalData;
    private final BiFunction<List<Double>, List<Double>, Double> inverseFunction;

    public FunctionValue(List<ExperimentalMeasurement> experimentalData, BiFunction<List<Double>, List<Double>, Double> inverseFunction) {
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

        List<ModelLayer> modelLayers = new ArrayList<>();
        for (int i = 0; i < currentModelPower.size(); i++) {
            modelLayers.add(ModelLayer.create(currentModelPower.get(i), currentModelResistance.get(i)));
        }

        List<Double> solvedResistance = ForwardSolver.createDefaultForwardSolver(
                experimentalData,
                modelLayers
        ).solve();

        return inverseFunction.apply(solvedResistance,
                experimentalData.stream().map(ExperimentalMeasurement::getResistanceApparent).collect(Collectors.toList()));
    }
}
