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
    //Экспериментальные точки для FS
    private final List<ExperimentalData> experimentalData;
    //Функция для вычисления разности между exp и theoretical точками
    private final BiFunction<List<Double>, List<Double>, Double> inverseFunction;
    //Исходная модель
    private final List<ModelLayer> modelLayers;

    public FunctionValue(List<ExperimentalData> experimentalData,
                         BiFunction<List<Double>, List<Double>, Double> inverseFunction,
                         List<ModelLayer> modelLayers) {
        this.experimentalData = experimentalData;
        this.inverseFunction = inverseFunction;
        this.modelLayers = modelLayers;
//        Тест
//        this.modelLayers = new ArrayList<>(modelLayers);
//        this.modelLayers.set(1, modelLayers.get(1).withFixedResistance(true));
//        this.modelLayers.set(4, modelLayers.get(4).withFixedResistance(true));
//        this.modelLayers.set(1, modelLayers.get(1).withFixedPower(true));
//        this.modelLayers.set(3, modelLayers.get(3).withFixedPower(true));
    }

    @Override
    public double value(double[] variables) {
        //Изменяемые сопротивления и мощности
        List<Double> currentModelResistance = new ArrayList<>();
        List<Double> currentModelPower = new ArrayList<>();

        int unfixedResistancesCnt = (int) modelLayers.stream()
                .filter(modelLayer -> !modelLayer.isFixedResistance()).count();

        int unfixedPowersCnt = (int) modelLayers.stream()
                .filter(modelLayer -> !modelLayer.isFixedPower()).count();

        //Восстановление изменяемых слоев до нормальной формы
        for (int i = 0; i < unfixedResistancesCnt; i++) {
            ModelLayer modelLayer = modelLayers.get(i);
            currentModelResistance.add(Math.exp(variables[i]));
        }
        for (int i = unfixedResistancesCnt; i < variables.length; i++) {
            ModelLayer modelLayer = modelLayers.get(i - (variables.length + 1) / 2);
            currentModelPower.add(Math.exp(variables[i]));
        }
        currentModelPower.add(0.0);

        //Объединение изменяемых и неизменяемых слоев (в нормальной форме)
        List<Double> newModelResistance = new ArrayList<>();
        List<Double> newModelPower = new ArrayList<>();

        int cntUnfixedResistances = 0;
        for (int i = 0; i < modelLayers.size(); i++) {
            ModelLayer modelLayer = modelLayers.get(i);
            if (modelLayer.isFixedResistance()) {
                newModelResistance.add(modelLayer.getResistance());
            } else {
                newModelResistance.add(currentModelResistance.get(cntUnfixedResistances));
                cntUnfixedResistances++;
            }
        }

        int cntUnfixedPowers = 0;
        for (int i = 0; i < modelLayers.size(); i++) {
            ModelLayer modelLayer = modelLayers.get(i);
            if (modelLayer.isFixedPower()) {
                newModelPower.add(modelLayer.getPower());
            } else {
                newModelPower.add(currentModelPower.get(cntUnfixedPowers));
                cntUnfixedPowers++;
            }
        }

        List<ModelLayer> newModelLayers = new ArrayList<>();
        for (int i = 0; i < modelLayers.size(); i++) {
            newModelLayers.add(ModelLayer.createNotFixed(newModelPower.get(i), newModelResistance.get(i)));
        }

        List<Double> solvedResistance = (List<Double>) ForwardSolverKt.ForwardSolver().invoke(experimentalData, newModelLayers);

        return inverseFunction.apply(solvedResistance,
                experimentalData.stream().map(ExperimentalData::getResistanceApparent).collect(Collectors.toList()));
    }
}
