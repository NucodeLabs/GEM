package ru.nucodelabs.geo.ves.calc.inverse;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import ru.nucodelabs.geo.target.TargetFunction;
import ru.nucodelabs.geo.ves.ExperimentalData;
import ru.nucodelabs.geo.ves.ModelLayer;
import ru.nucodelabs.geo.ves.calc.forward.ForwardSolver;
import ru.nucodelabs.geo.ves.calc.inverse.inverse_functions.FunctionValue;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
    private final TargetFunction.WithError targetFunction;

    @Inject
    public InverseSolver(ForwardSolver forwardSolver, TargetFunction.WithError targetFunction) {
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
            TargetFunction.WithError targetFunction
    ) {
        this.sideLength = sideLength;
        this.relativeThreshold = relativeThreshold;
        this.absoluteThreshold = absoluteThreshold;
        this.forwardSolver = forwardSolver;
        this.targetFunction = targetFunction;
    }

    private void setLimitValues(
            List<Double> resistances, double minResistance, double maxResistance,
            List<Double> powers, double minPower, double maxPower
    ) {
        for (int i = 0; i < resistances.size(); i++) {
            if (resistances.get(i) < minResistance)
                resistances.set(i, minResistance);
            else if (resistances.get(i) > maxResistance)
                resistances.set(i, maxResistance);
        }
        for (int i = 0; i < powers.size(); i++) {
            if (powers.get(i) != 0 && powers.get(i) < minPower)
                powers.set(i, minPower);
            else if (powers.get(i) > maxPower)
                powers.set(i, maxPower);
        }
    }

    public List<ModelLayer> getOptimizedModelData(
            List<ExperimentalData> experimentalData,
            List<ModelLayer> modelData,
            final int maxEval
    ) {

        //Изменяемые сопротивления и мощности
        List<Double> modelResistance = modelData.stream()
                .filter(modelLayer -> !modelLayer.isFixedResistance()).map(ModelLayer::getResistance).collect(Collectors.toList());
        List<Double> modelPower = modelData.stream()
                .filter(modelLayer -> !modelLayer.isFixedPower()).map(ModelLayer::getPower).collect(Collectors.toList());

        //Установка ограничений для адекватности обратной задачи
        double minPower = experimentalData.stream()
                .map(ExperimentalData::getAb2)
                .mapToDouble(Double::doubleValue)
                .min()
                .orElseThrow(NoSuchElementException::new);
        double maxPower = experimentalData.stream()
                .map(ExperimentalData::getAb2)
                .mapToDouble(Double::doubleValue)
                .max()
                .orElseThrow(NoSuchElementException::new);

        setLimitValues(
                modelResistance, 0.1, 1e5,
                modelPower, 0.1, maxPower
        );

        //Неизменяемые сопротивления и мощности
        List<Double> fixedModelResistance = modelData.stream()
                .filter(ModelLayer::isFixedResistance).map(ModelLayer::getResistance).toList();
        List<Double> fixedModelPower = modelData.stream()
                .filter(ModelLayer::isFixedPower).map(ModelLayer::getPower).toList();

        SimplexOptimizer optimizer = new SimplexOptimizer(relativeThreshold, absoluteThreshold);

        MultivariateFunction multivariateFunction = new FunctionValue(
                experimentalData,
                targetFunction,
                modelData,
                forwardSolver
        );

        //anyArray = resistance.size...(model.size - 1)
        int dimension = modelResistance.size() + modelPower.size() - 1; // -1 - мощность последнего слоя не передается как параметр
        NelderMeadSimplex nelderMeadSimplex = new NelderMeadSimplex(dimension, sideLength);

        double[] startPoint = new double[dimension]; //res_1, ..., res_n, power_1, ..., power_n-1
        for (int i = 0; i < modelResistance.size(); i++) {
            startPoint[i] = Math.log(modelResistance.get(i));
        }
        for (int i = modelResistance.size(); i < dimension; i++) {
            startPoint[i] = Math.log(modelPower.get(i - modelResistance.size()));
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
        List<Double> newModelResistance = new ArrayList<>();

        int cntFixedResistances = 0;
        int cntUnfixedResistances = 0;
        for (ModelLayer modelLayer : modelData) {
            if (modelLayer.isFixedResistance()) {
                newModelResistance.add(fixedModelResistance.get(cntFixedResistances));
                cntFixedResistances++;
            } else {
                newModelResistance.add(Math.exp(key[cntUnfixedResistances]));
                cntUnfixedResistances++;
            }
        }

        int cntFixedPowers = 0;
        int cntUnfixedPowers = 0;
        for (int i = 0; i < modelData.size() - 1; i++) {
            ModelLayer modelLayer = modelData.get(i);
            int shift = modelResistance.size();
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
                    newModelResistance.get(i),
                    modelData.get(i).isFixedPower(),
                    modelData.get(i).isFixedResistance())
            );
        }

        return resultModel;
    }
}
