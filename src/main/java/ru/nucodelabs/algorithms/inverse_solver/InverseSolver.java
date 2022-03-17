package ru.nucodelabs.algorithms.inverse_solver;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;

import java.util.ArrayList;
import java.util.List;

public class InverseSolver {
    private final Picket picket;
    private final double sideLength;
    private final double relativeThreshold;
    private final double absoluteThreshold;
    private final MultivariateFunction multivariateFunction;

    public InverseSolver(Picket picket, double sideLength, double relativeThreshold, double absoluteThreshold, MultivariateFunction multivariateFunction) {
        this.picket = picket;
        this.sideLength = sideLength;
        this.relativeThreshold = relativeThreshold;
        this.absoluteThreshold = absoluteThreshold;
        this.multivariateFunction = multivariateFunction;
    }

    public ModelData getOptimizedPicket() {
        final int MAX_EVAL = 10000;

        ModelData modelData = picket.modelData();

        List<Double> modelResistance = modelData.resistance();
        List<Double> modelPower = modelData.power();

        int dimension = modelData.getSize() * 2 - 1; // -1 - мощность последнего слоя не передается как параметр
        NelderMeadSimplex nelderMeadSimplex = new NelderMeadSimplex(dimension, sideLength); //power_1, res_1, ..., power_n, res_n

        SimplexOptimizer optimizer = new SimplexOptimizer(relativeThreshold, absoluteThreshold);

        double[] startPoint = new double[dimension]; //power_1, res_1, ..., power_(n - 1), res_(n - 1), res_n
        //power_n = 0, поэтому используем 2n - 1 размерность. При нахождении значения функции подставлять 0
        for (int i = 0; i < dimension - 1; i += 2) {
            startPoint[i] = Math.log(modelPower.get(i / 2));
            startPoint[i + 1] = Math.log(modelResistance.get(i / 2));
        }
        startPoint[dimension - 1] = Math.log(modelResistance.get((dimension - 1) / 2));

        InitialGuess initialGuess = new InitialGuess(startPoint);

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

        for (int i = 0; i < key.length - 1; i += 2) {
            newModelPower.add(Math.exp(key[i]));
            newModelResistance.add(Math.exp(key[i + 1]));
        }

        newModelPower.add(0.0);
        newModelResistance.add(Math.exp(key[key.length - 1]));

        return new ModelData(newModelResistance, modelData.polarization(), newModelPower);
    }
}
