package ru.nucodelabs.geo.ves.calc.inverse;

import jakarta.inject.Inject;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import ru.nucodelabs.geo.forward.ForwardSolver;
import ru.nucodelabs.geo.target.RelativeErrorAwareTargetFunction;
import ru.nucodelabs.geo.ves.ModelLayer;
import ru.nucodelabs.geo.ves.ReadOnlyExperimentalSignal;
import ru.nucodelabs.geo.ves.ReadOnlyModelLayer;
import ru.nucodelabs.geo.ves.calc.inverse.func.FunctionValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InverseSolver {

    //Размер симплекса (по каждому измерению)
    public static final double SIDE_LENGTH_DEFAULT = 1.0;

    //Какие-то константы для SimplexOptimize
    public static final double RELATIVE_THRESHOLD_DEFAULT = 1e-10;
    public static final double ABSOLUTE_THRESHOLD_DEFAULT = 1e-30;

    public static final int MAX_EVAL_DEFAULT = 100000;

    private final double sideLength;
    private final double relativeThreshold;
    private final double absoluteThreshold;
    private final ForwardSolver forwardSolver;
    private final RelativeErrorAwareTargetFunction targetFunction;

    @Inject
    public InverseSolver(ForwardSolver forwardSolver, RelativeErrorAwareTargetFunction targetFunction) {
        this(
                SIDE_LENGTH_DEFAULT,
                RELATIVE_THRESHOLD_DEFAULT,
                ABSOLUTE_THRESHOLD_DEFAULT,
                forwardSolver,
                targetFunction
        );
    }

    public InverseSolver(
            double sideLength,
            double relativeThreshold,
            double absoluteThreshold,
            ForwardSolver forwardSolver,
            RelativeErrorAwareTargetFunction targetFunction
    ) {
        this.sideLength = sideLength;
        this.relativeThreshold = relativeThreshold;
        this.absoluteThreshold = absoluteThreshold;
        this.forwardSolver = forwardSolver;
        this.targetFunction = targetFunction;
    }

    private void setLimitValues(
        List<Double> resistivity, double minResistivity, double maxResistivity,
            List<Double> powers, double minPower, double maxPower
    ) {
        for (int i = 0; i < resistivity.size(); i++) {
            if (resistivity.get(i) < minResistivity)
                resistivity.set(i, minResistivity);
            else if (resistivity.get(i) > maxResistivity)
                resistivity.set(i, maxResistivity);
        }
        for (int i = 0; i < powers.size(); i++) {
            if (powers.get(i) != 0 && powers.get(i) < minPower)
                powers.set(i, minPower);
            else if (powers.get(i) > maxPower)
                powers.set(i, maxPower);
        }
    }

    public List<ModelLayer> getOptimizedModelData(
        List<ReadOnlyExperimentalSignal> experimentalData,
        List<ReadOnlyModelLayer> modelData,
            final int maxEval
    ) {

        //Изменяемые сопротивления и мощности
        List<Double> modelResistivity = modelData.stream()
            .filter(modelLayer -> !modelLayer.isFixedResistivity()).map(ReadOnlyModelLayer::getResistivity).collect(Collectors.toList());
        List<Double> modelPower = modelData.stream()
            .filter(modelLayer -> !modelLayer.isFixedPower()).map(ReadOnlyModelLayer::getPower).collect(Collectors.toList());

        //Установка ограничений для адекватности обратной задачи
        double maxPower = experimentalData.stream()
            .map(ReadOnlyExperimentalSignal::getAb2)
                .mapToDouble(Double::doubleValue)
                .max()
            .orElseThrow();

        setLimitValues(
            modelResistivity, 0.1, 1e5,
                modelPower, 0.1, maxPower
        );

        //Неизменяемые сопротивления и мощности
        List<Double> fixedModelResistivity = modelData.stream()
            .filter(ReadOnlyModelLayer::isFixedResistivity).map(ReadOnlyModelLayer::getResistivity).toList();
        List<Double> fixedModelPower = modelData.stream()
            .filter(ReadOnlyModelLayer::isFixedPower).map(ReadOnlyModelLayer::getPower).toList();

        SimplexOptimizer optimizer = new SimplexOptimizer(relativeThreshold, absoluteThreshold);

        MultivariateFunction multivariateFunction = new FunctionValue(
                experimentalData,
                targetFunction,
                modelData,
                forwardSolver
        );

        //anyArray = resistivity.size...(model.size - 1)
        int dimension = modelResistivity.size() + modelPower.size() - 1; // -1 - мощность последнего слоя не передается как параметр
        NelderMeadSimplex nelderMeadSimplex = new NelderMeadSimplex(dimension, sideLength);

        double[] startPoint = new double[dimension]; //res_1, ..., res_n, power_1, ..., power_n-1
        for (int i = 0; i < modelResistivity.size(); i++) {
            startPoint[i] = Math.log(modelResistivity.get(i));
        }
        for (int i = modelResistivity.size(); i < dimension; i++) {
            startPoint[i] = Math.log(modelPower.get(i - modelResistivity.size()));
        }

        InitialGuess initialGuess = new InitialGuess(startPoint);

        //Передавать только изменяемые параметры
        PointValuePair pointValuePair = optimizer.optimize(
                new MaxEval(maxEval),
                new ObjectiveFunction(multivariateFunction),
                GoalType.MINIMIZE,
                initialGuess,
                nelderMeadSimplex
        );

        double[] key = pointValuePair.getKey();

        List<Double> newModelPower = new ArrayList<>();
        List<Double> newModelResistivity = new ArrayList<>();

        int cntFixedResistivity = 0;
        int cntUnfixedResistivity = 0;
        for (var modelLayer : modelData) {
            if (modelLayer.isFixedResistivity()) {
                newModelResistivity.add(fixedModelResistivity.get(cntFixedResistivity));
                cntFixedResistivity++;
            } else {
                newModelResistivity.add(Math.exp(key[cntUnfixedResistivity]));
                cntUnfixedResistivity++;
            }
        }

        int cntFixedPowers = 0;
        int cntUnfixedPowers = 0;
        for (int i = 0; i < modelData.size() - 1; i++) {
            var modelLayer = modelData.get(i);
            int shift = modelResistivity.size();
            if (modelLayer.isFixedPower()) {
                newModelPower.add(fixedModelPower.get(cntFixedPowers));
                cntFixedPowers++;
            } else {
                newModelPower.add(Math.exp(key[cntUnfixedPowers + shift]));
                cntUnfixedPowers++;
            }
        }
        newModelPower.add(0.0); //Для последнего слоя

        List<ModelLayer> resultModel = new ArrayList<>();

        for (int i = 0; i < modelData.size(); i++) {
            resultModel.add(new ModelLayer(
                    newModelPower.get(i),
                newModelResistivity.get(i),
                    modelData.get(i).isFixedPower(),
                modelData.get(i).isFixedResistivity())
            );
        }

        return resultModel;
    }
}
