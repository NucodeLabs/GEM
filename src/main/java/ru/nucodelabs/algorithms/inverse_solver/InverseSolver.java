package ru.nucodelabs.algorithms.inverse_solver;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import ru.nucodelabs.algorithms.inverse_solver.inverse_functions.FunctionValue;
import ru.nucodelabs.algorithms.inverse_solver.inverse_functions.SquaresDiff;
import ru.nucodelabs.data.ves.ModelLayer;
import ru.nucodelabs.data.ves.Picket;

import java.util.ArrayList;
import java.util.List;

public class InverseSolver {

    //Размер симплекса (по каждому измерению)
    private static final double SIDE_LENGTH_DEFAULT = 0.1;

    //Какие-то константы для SimplexOptimize
    private static final double RELATIVE_THRESHOLD_DEFAULT = 1e-10;
    private static final double ABSOLUTE_THRESHOLD_DEFAULT = 1e-30;

    private final Picket picket;
    private final double sideLength;
    private final double relativeThreshold;
    private final double absoluteThreshold;
    private MultivariateFunction multivariateFunction;

    public InverseSolver(Picket picket) {
        this(
                picket,
                SIDE_LENGTH_DEFAULT,
                RELATIVE_THRESHOLD_DEFAULT,
                ABSOLUTE_THRESHOLD_DEFAULT
        );
    }

    public InverseSolver(
            Picket picket,
            double sideLength,
            double relativeThreshold,
            double absoluteThreshold) {
        this.picket = picket;
        this.sideLength = sideLength;
        this.relativeThreshold = relativeThreshold;
        this.absoluteThreshold = absoluteThreshold;
    }

    public List<ModelLayer> getOptimizedModelData() {
        final int MAX_EVAL = 1000000;

        List<ModelLayer> modelData = picket.getModelData();

//        Тест
//        modelData = new ArrayList<>(picket.getModelData());
//        modelData.set(1, modelData.get(1).withFixedResistance(true));
//        modelData.set(4, modelData.get(4).withFixedResistance(true));
//        modelData.set(1, modelData.get(1).withFixedPower(true));
//        modelData.set(3, modelData.get(3).withFixedPower(true));

        //Изменяемые сопротивления и мощности
        List<Double> modelResistance = modelData.stream()
                .filter(modelLayer -> !modelLayer.isFixedResistance()).map(ModelLayer::getResistance).toList();
        List<Double> modelPower = modelData.stream()
                .filter(modelLayer -> !modelLayer.isFixedPower()).map(ModelLayer::getPower).toList();

        //Неизменяемые сопротивления и мощности
        List<Double> fixedModelResistance = modelData.stream()
                .filter(ModelLayer::isFixedResistance).map(ModelLayer::getResistance).toList();
        List<Double> fixedModelPower = modelData.stream()
                .filter(ModelLayer::isFixedPower).map(ModelLayer::getPower).toList();

        multivariateFunction = new FunctionValue(
                picket.getExperimentalData(), new SquaresDiff(), modelData
        );

        //anyArray = resistance.size...(model.size - 1)
        int dimension = modelResistance.size() + modelPower.size() - 1; // -1 - мощность последнего слоя не передается как параметр
        NelderMeadSimplex nelderMeadSimplex = new NelderMeadSimplex(dimension, sideLength);

        SimplexOptimizer optimizer = new SimplexOptimizer(relativeThreshold, absoluteThreshold);

        double[] startPoint = new double[dimension]; //res_1, ..., res_n, power_1, ..., power_n-1
        for (int i = 0; i < modelResistance.size(); i++) {
            startPoint[i] = Math.log(modelResistance.get(i));
        }
        for (int i = modelResistance.size(); i < modelResistance.size() + modelPower.size() - 1; i++) {
            startPoint[i] = Math.log(modelPower.get(i - modelResistance.size()));
        }

        InitialGuess initialGuess = new InitialGuess(startPoint);

        //Передавать только изменяемые параметры
        PointValuePair pointValuePair = optimizer.optimize(
                new MaxEval(MAX_EVAL),
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
        for (int i = 0; i < modelData.size(); i++) {
            ModelLayer modelLayer = modelData.get(i);
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
            resultModel.add(ModelLayer.createNotFixed(newModelPower.get(i), newModelResistance.get(i)));
        }

        return resultModel;
    }
}
