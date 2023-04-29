package ru.nucodelabs.geo.ves.calc.inverse.inverse_functions;

import org.apache.commons.math3.analysis.MultivariateFunction;
import ru.nucodelabs.geo.target.TargetFunction;
import ru.nucodelabs.geo.ves.ExperimentalData;
import ru.nucodelabs.geo.ves.ModelLayer;
import ru.nucodelabs.geo.ves.calc.forward.ForwardSolver;

import java.util.ArrayList;
import java.util.List;

public class FunctionValue implements MultivariateFunction {
    //Экспериментальные точки для FS
    private final List<ExperimentalData> experimentalData;
    //Функция для вычисления разности между exp и theoretical точками
    private final TargetFunction.WithError targetFunction;
    //Исходная модель
    private final List<ModelLayer> modelLayers;
    private final ForwardSolver forwardSolver;

    private double diffMinValue = Double.MAX_VALUE;

    public FunctionValue(List<ExperimentalData> experimentalData,
                         TargetFunction.WithError targetFunction,
                         List<ModelLayer> modelLayers,
                         ForwardSolver forwardSolver) {
        this.experimentalData = experimentalData;
        this.targetFunction = targetFunction;
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

        double diffValue = targetFunction.apply(
                solvedResistance,
                experimentalData.stream().map(ExperimentalData::getResistanceApparent).toList(),
                experimentalData.stream().map(ExperimentalData::getErrorResistanceApparent).toList()
        );

        boolean flag = false;

        for (ModelLayer modelLayer : newModelLayers) {
            if (modelLayer.getResistance() < 0.1 ||
                    modelLayer.getResistance() > 1e5 ||
                    (modelLayer.getPower() != 0.0 && modelLayer.getPower() < 0.1) ||
                    modelLayer.getPower() > experimentalData.get(experimentalData.size() - 1).getAb2()) {
                diffValue = Math.max(diffMinValue * (1.1 + 0.1 * Math.random()), diffValue);
                flag = true;
                break;
            }
        }

        if (!flag)
            diffMinValue = Math.min(diffValue, diffMinValue);

        return diffValue;
    }
}
