package ru.nucodelabs.algorithms.inverse_solver.inverse_functions;

import org.apache.commons.math3.analysis.MultivariateFunction;
import ru.nucodelabs.algorithms.forward_solver.ForwardSolverKt;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class FunctionValue implements MultivariateFunction {
    private final List<ExperimentalData> experimentalData;
    private final BiFunction<List<Double>, List<Double>, Double> inverseFunction;
    private final List<ModelLayer> modelLayers;

    public FunctionValue(List<ExperimentalData> experimentalData,
                         BiFunction<List<Double>, List<Double>, Double> inverseFunction,
                         List<ModelLayer> modelLayers) {
        this.experimentalData = experimentalData;
        this.inverseFunction = inverseFunction;
        //this.modelLayers = modelLayers;
        //TODO: тест
        this.modelLayers = new ArrayList<>(modelLayers);
        this.modelLayers.set(1, modelLayers.get(1).withFixedPower(true));
    }

    @Override
    public double value(double[] variables) {
        //Изменяемые сопротивления и мощности
        List<Double> currentModelResistance = new ArrayList<>();
        List<Double> currentModelPower = new ArrayList<>();

        for (int i = 0; i < (variables.length + 1) / 2; i++) {
            ModelLayer modelLayer = modelLayers.get(i);
            currentModelResistance.add(Math.exp(variables[i]));
        }
        for (int i = (variables.length + 1) / 2; i < variables.length; i++) {
            ModelLayer modelLayer = modelLayers.get(i - (variables.length + 1) / 2);
            currentModelPower.add(Math.exp(variables[i]));
        }
        currentModelPower.add(0.0);

        //TODO: Сделать добавление фиксированных слоев
        List<ModelLayer> modelLayers = new ArrayList<>();
        for (int i = 0; i < currentModelPower.size(); i++) {
            modelLayers.add(ModelLayer.createNotFixed(currentModelPower.get(i), currentModelResistance.get(i)));
        }

        List<Double> solvedResistance = (List<Double>) ForwardSolverKt.ForwardSolver().invoke(experimentalData, modelLayers);

        return inverseFunction.apply(solvedResistance,
                experimentalData.stream().map(ExperimentalData::getResistanceApparent).collect(Collectors.toList()));
    }
}
