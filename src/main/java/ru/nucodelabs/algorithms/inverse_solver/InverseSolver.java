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
import ru.nucodelabs.data.ves.ModelData;
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
    private final MultivariateFunction multivariateFunction;

    public InverseSolver(Picket picket) {
        this(
                picket,
                SIDE_LENGTH_DEFAULT,
                RELATIVE_THRESHOLD_DEFAULT,
                ABSOLUTE_THRESHOLD_DEFAULT,
                new FunctionValue(
                        picket.experimentalData(),
                        new SquaresDiff())
        );
    }

    public InverseSolver(
            Picket picket,
            double sideLength,
            double relativeThreshold,
            double absoluteThreshold,
            MultivariateFunction multivariateFunction) {
        this.picket = picket;
        this.sideLength = sideLength;
        this.relativeThreshold = relativeThreshold;
        this.absoluteThreshold = absoluteThreshold;
        this.multivariateFunction = multivariateFunction;
    }

    public ModelData getOptimizedModelData() {
        final int MAX_EVAL = 1000000;

        ModelData modelData = picket.modelData();

        List<Double> modelResistance = modelData.resistance();
        List<Double> modelPower = modelData.power();

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

        for (int i = 0; i < modelResistance.size(); i++) {
            newModelResistance.add(Math.exp(key[i]));
        }
        for (int i = modelResistance.size(); i < modelResistance.size() + modelPower.size() - 1; i++) {
            newModelPower.add(Math.exp(key[i]));
        }
        newModelPower.add(0.0); //Для последнего слоя

        return new ModelData(newModelResistance, modelData.polarization(), newModelPower);
    }
}
