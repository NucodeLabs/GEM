package ru.nucodelabs.geo.ves.calc.inverse.func;

import org.apache.commons.math3.analysis.MultivariateFunction;
import ru.nucodelabs.geo.forward.ForwardSolver;
import ru.nucodelabs.geo.target.RelativeErrorAwareTargetFunction;
import ru.nucodelabs.geo.ves.ModelLayer;
import ru.nucodelabs.geo.ves.ReadOnlyExperimentalSignal;
import ru.nucodelabs.geo.ves.ReadOnlyModelLayer;
import ru.nucodelabs.geo.ves.calc.adapter.ForwardSolverAdapterKt;

import java.util.ArrayList;
import java.util.List;

public class FunctionValue implements MultivariateFunction {
    //Экспериментальные точки для FS
    private final List<ReadOnlyExperimentalSignal> experimentalData;
    //Функция для вычисления разности между exp и theoretical точками
    private final RelativeErrorAwareTargetFunction targetFunction;
    //Исходная модель
    private final List<ReadOnlyModelLayer> modelLayers;
    private final ForwardSolver forwardSolver;

    private double diffMinValue = Double.MAX_VALUE;

    public FunctionValue(List<ReadOnlyExperimentalSignal> experimentalData,
                         RelativeErrorAwareTargetFunction targetFunction,
                         List<ReadOnlyModelLayer> modelLayers,
                         ForwardSolver forwardSolver) {
        this.experimentalData = experimentalData;
        this.targetFunction = targetFunction;
        this.modelLayers = modelLayers;
        this.forwardSolver = forwardSolver;
    }

    @Override
    public double value(double[] variables) {
        //Изменяемые сопротивления и мощности
        List<Double> currentModelResistivity = new ArrayList<>();
        List<Double> currentModelPower = new ArrayList<>();

        int unfixedResistivityCnt = (int) modelLayers.stream()
            .filter(modelLayer -> !modelLayer.isFixedResistivity()).count();

        //Восстановление изменяемых слоев до нормальной формы
        for (int i = 0; i < unfixedResistivityCnt; i++) {
            currentModelResistivity.add(Math.exp(variables[i]));
        }
        for (int i = unfixedResistivityCnt; i < variables.length; i++) {
            currentModelPower.add(Math.exp(variables[i]));
        }
        currentModelPower.add(0.0);

        //Объединение изменяемых и неизменяемых слоев (в нормальной форме)
        List<Double> newModelResistivity = new ArrayList<>();
        List<Double> newModelPower = new ArrayList<>();

        int cntUnfixedResistivity = 0;
        for (var modelLayer : modelLayers) {
            if (modelLayer.isFixedResistivity()) {
                newModelResistivity.add(modelLayer.getResistivity());
            } else {
                newModelResistivity.add(currentModelResistivity.get(cntUnfixedResistivity));
                cntUnfixedResistivity++;
            }
        }

        int cntUnfixedPowers = 0;
        for (var modelLayer : modelLayers) {
            if (modelLayer.isFixedPower()) {
                newModelPower.add(modelLayer.getPower());
            } else {
                newModelPower.add(currentModelPower.get(cntUnfixedPowers));
                cntUnfixedPowers++;
            }
        }

        List<ModelLayer> newModelLayers = new ArrayList<>();
        for (int i = 0; i < modelLayers.size(); i++) {
            newModelLayers.add(new ModelLayer(newModelPower.get(i), newModelResistivity.get(i), false, false));
        }

        List<Double> solvedResistivity = ForwardSolverAdapterKt.invoke(forwardSolver, experimentalData, newModelLayers);

        double diffValue = targetFunction.invoke(
            solvedResistivity,
            experimentalData.stream().map(ReadOnlyExperimentalSignal::getResistivityApparent).toList(),
            experimentalData.stream().map(ReadOnlyExperimentalSignal::getErrorResistivityApparent).toList()
        );

        boolean flag = false;

        for (ModelLayer modelLayer : newModelLayers) {
            if (modelLayer.getResistivity() < 0.1 ||
                modelLayer.getResistivity() > 1e5 ||
                    (modelLayer.getPower() != 0.0 && modelLayer.getPower() < 0.1) ||
                modelLayer.getPower() > experimentalData.getLast().getAb2()) {
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
