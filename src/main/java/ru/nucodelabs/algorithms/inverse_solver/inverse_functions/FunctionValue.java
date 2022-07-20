package ru.nucodelabs.algorithms.inverse_solver.inverse_functions;

import org.apache.commons.math3.analysis.MultivariateFunction;
import ru.nucodelabs.algorithms.forward_solver.ForwardSolver;
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
    private final ForwardSolver forwardSolver;

    private double diffMinValue = Double.MAX_VALUE;

    public FunctionValue(List<ExperimentalData> experimentalData,
                         BiFunction<List<Double>, List<Double>, Double> inverseFunction,
                         List<ModelLayer> modelLayers,
                         ForwardSolver forwardSolver) {
        this.experimentalData = experimentalData;
        this.inverseFunction = inverseFunction;
        this.modelLayers = modelLayers;
        this.forwardSolver = forwardSolver;
    }

    @Override
    public double value(double[] variables) {
        //Изменяемые сопротивления и мощности
        List<Double> currentModelResistance = new ArrayList<>();
        List<Double> currentModelPower = new ArrayList<>();

        int unfixedResistancesCnt = (int) modelLayers.stream()
                .filter(modelLayer -> !modelLayer.isFixedResistance()).count();

        //Восстановление изменяемых слоев до нормальной формы
        for (int i = 0; i < unfixedResistancesCnt; i++) {
            currentModelResistance.add(Math.exp(variables[i]));
        }
        for (int i = unfixedResistancesCnt; i < variables.length; i++) {
            currentModelPower.add(Math.exp(variables[i]));
        }
        currentModelPower.add(0.0);

        //Объединение изменяемых и неизменяемых слоев (в нормальной форме)
        List<Double> newModelResistance = new ArrayList<>();
        List<Double> newModelPower = new ArrayList<>();

        int cntUnfixedResistances = 0;
        for (ModelLayer modelLayer : modelLayers) {
            if (modelLayer.isFixedResistance()) {
                newModelResistance.add(modelLayer.getResistance());
            } else {
                newModelResistance.add(currentModelResistance.get(cntUnfixedResistances));
                cntUnfixedResistances++;
            }
        }

        int cntUnfixedPowers = 0;
        for (ModelLayer modelLayer : modelLayers) {
            if (modelLayer.isFixedPower()) {
                newModelPower.add(modelLayer.getPower());
            } else {
                newModelPower.add(currentModelPower.get(cntUnfixedPowers));
                cntUnfixedPowers++;
            }
        }

        List<ModelLayer> newModelLayers = new ArrayList<>();
        for (int i = 0; i < modelLayers.size(); i++) {
            newModelLayers.add(new ModelLayer(newModelPower.get(i), newModelResistance.get(i), false, false));
        }

        List<Double> solvedResistance = forwardSolver.invoke(experimentalData, newModelLayers);

        double diffValue = inverseFunction.apply(solvedResistance,
                experimentalData.stream().map(ExperimentalData::getResistanceApparent).collect(Collectors.toList()));

        boolean flag = false;

        for (ModelLayer modelLayer : newModelLayers) {
            if (modelLayer.getResistance() < 0.1 ||
                    modelLayer.getResistance() > 1e5 ||
                    (modelLayer.getPower() != 0.0 && modelLayer.getPower() < experimentalData.get(0).getAb2() / 2.0) ||
                    modelLayer.getPower() > experimentalData.get(experimentalData.size() - 1).getAb2()) {
                diffValue = Math.max(diffMinValue * 1.1, diffValue);
                flag = true;
                break;
            }
        }

        if (!flag)
            diffMinValue = Math.min(diffValue, diffMinValue);

        return diffValue;
    }
}
