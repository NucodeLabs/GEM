package ru.nucodelabs.algorithms;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.junit.jupiter.api.Test;
import ru.nucodelabs.ShiraPicket;
import ru.nucodelabs.algorithms.inverse_solver.InverseSolver;
import ru.nucodelabs.algorithms.inverse_solver.inverse_functions.FunctionValue;
import ru.nucodelabs.algorithms.inverse_solver.inverse_functions.SquaresDiff;
import ru.nucodelabs.data.ves.ModelLayer;
import ru.nucodelabs.data.ves.Picket;

import java.util.List;

public class InverseSolverTest {
    @Test
    void inverseSolverTest_1() throws Exception {
        Picket picket = ShiraPicket.getPicket();

        final double SIDE_LENGTH = 0.1;
        final double RELATIVE_THRESHOLD = 1e-10;
        double ABSOLUTE_THRESHOLD = 1e-30;
        MultivariateFunction multivariateFunction = new FunctionValue(
                picket.getExperimentalData(),
                new SquaresDiff());
        InverseSolver inverseSolver = new InverseSolver(picket, SIDE_LENGTH, RELATIVE_THRESHOLD, ABSOLUTE_THRESHOLD, multivariateFunction);
        List<ModelLayer> modelData = inverseSolver.getOptimizedModelData();
        modelData.forEach(System.out::println);
    }
}
