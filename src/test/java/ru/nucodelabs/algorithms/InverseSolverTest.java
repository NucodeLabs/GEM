package ru.nucodelabs.algorithms;

import org.junit.jupiter.api.Test;
import ru.nucodelabs.ShiraPicket;
import ru.nucodelabs.geo.ves.calc.forward.ForwardSolverKt;
import ru.nucodelabs.geo.ves.calc.inverse.InverseSolver;
import ru.nucodelabs.geo.ves.ModelLayer;
import ru.nucodelabs.geo.ves.Picket;

import java.util.List;

public class InverseSolverTest {
    @Test
    void inverseSolverTest_1() throws Exception {
        Picket picket = ShiraPicket.getPicket();

        InverseSolver inverseSolver = new InverseSolver(ForwardSolverKt.ForwardSolver());
        List<ModelLayer> modelData = inverseSolver.getOptimizedModelData(picket);
        modelData.forEach(System.out::println);
    }
}
