package ru.nucodelabs.algorithms.inverseSolver;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import ru.nucodelabs.algorithms.ForwardSolver;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InverseSolver {
    static ExperimentalData experimentalData;
    static ModelData modelData;

    static int debugCnt = 0;

    static List<Double> experimentalResistance, experimentalAB_2, modelResistance, modelPower;
    private InverseSolver() {

    }

    public static ModelData getOptimizedPicket(Picket picket) {
        experimentalData = picket.experimentalData();
        modelData = picket.modelData();

        experimentalResistance = experimentalData.resistanceApparent();
        experimentalAB_2 = experimentalData.ab_2();

        modelResistance = modelData.resistance();
        modelPower = modelData.power();

        int dimension = modelData.getSize() * 2;
        double sideLength = 0.00001; //Надо настраивать, а лучше использовать другую сигнатуру
        NelderMeadSimplex nelderMeadSimplex = new NelderMeadSimplex(dimension, sideLength); //power_1, res_1, ..., power_n, res_n

        SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-30);

        final FunctionValue functionValue = new FunctionValue();
        double[] startPoint = new double[dimension]; //power_1, res_1, ..., power_n, res_n
        //Начальная модель
        for (int i = 0; i < dimension; i += 2) {
            startPoint[i] = modelPower.get(i / 2);
            startPoint[i + 1] = modelResistance.get(i / 2);
        }
        InitialGuess initialGuess = new InitialGuess(startPoint);

        //Выходит из алгоритма, как и при достижении MaxEval
        double[] lowerBounds = new double[dimension];
        Arrays.fill(lowerBounds, 0);

        double[] upperBounds = new double[dimension];
        Arrays.fill(upperBounds, 1000);

        PointValuePair pointValuePair = optimizer.optimize(
                new MaxEval(10000),
                new ObjectiveFunction(functionValue),
                GoalType.MINIMIZE,
                initialGuess,
                nelderMeadSimplex//, new SimpleBounds(lowerBounds, upperBounds)
        );

        double[] key = pointValuePair.getKey();

        List<Double> newModelPower = new ArrayList<>();
        List<Double> newModelResistance = new ArrayList<>();

        for (int i = 0; i < key.length; i += 2) {
            newModelPower.add(key[i]);
            newModelResistance.add(key[i + 1]);
        }

        ModelData newModelData = new ModelData(newModelResistance, modelData.polarization(), newModelPower);

        return newModelData;
    }

    private static class FunctionValue implements MultivariateFunction {

        /**
         * Надо вынести в отдельный класс для модульности
         * Функция вычисляющая значение в n-мерном пространстве для массива отклонений теоретической от экспереминтальной кривой длины n
         *
         * @param variables Массив вида: power_1, res_1, ..., power_n, res_n, где n - число слоев модели
         * @return Значение функции в точке
         */
        @Override
        public double value(double[] variables) {
            double functionValue = 0;

            List<Double> currentModelPower = new ArrayList<>();
            List<Double> currentModelResistance = new ArrayList<>();

            for (int i = 0; i < variables.length; i += 2) {
                currentModelPower.add(variables[i]);
                currentModelResistance.add(variables[i + 1]);
            }

            List<Double> solvedResistance = ForwardSolver.ves(currentModelResistance, currentModelPower, experimentalAB_2);

            if (solvedResistance.size() != experimentalResistance.size()) {
                throw new RuntimeException("solvedResistance.size() != experimentalResistance.size()");
            }

            for (int i = 0; i < solvedResistance.size(); i++) {
                functionValue += Math.abs(Math.pow(solvedResistance.get(i), 2) - Math.pow(experimentalResistance.get(i), 2));
            }

            debugCnt++;
            return functionValue;
        }
    }
}
