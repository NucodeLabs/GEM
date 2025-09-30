package ru.nucodelabs.algorithms;

import org.junit.jupiter.api.Test;
import ru.nucodelabs.ShiraPicket;
import ru.nucodelabs.geo.forward.impl.SonetForwardSolverAdapter;
import ru.nucodelabs.geo.target.impl.SquareDiffTargetFunction;
import ru.nucodelabs.geo.ves.ModelLayer;
import ru.nucodelabs.geo.ves.Picket;
import ru.nucodelabs.geo.ves.ReadOnlyExperimentalSignal;
import ru.nucodelabs.geo.ves.ReadOnlyModelLayer;
import ru.nucodelabs.geo.ves.calc.inverse.InverseSolver;

import java.util.List;

public class InverseSolverTest {
    @Test
    void inverseSolverTest_1() throws Exception {
        Picket picket = ShiraPicket.getPicket();

        final double SIDE_LENGTH = 0.1;
        final double RELATIVE_THRESHOLD = 1e-10;
        double ABSOLUTE_THRESHOLD = 1e-30;
        InverseSolver inverseSolver = new InverseSolver(SIDE_LENGTH, RELATIVE_THRESHOLD, ABSOLUTE_THRESHOLD, new SonetForwardSolverAdapter(), new SquareDiffTargetFunction());
        List<ModelLayer> modelData = inverseSolver.getOptimizedModelData(
            picket.getEffectiveExperimentalData().stream().map(ReadOnlyExperimentalSignal.class::cast).toList(),
            picket.getModelData().stream().map(ReadOnlyModelLayer.class::cast).toList(),
            InverseSolver.MAX_EVAL_DEFAULT
        );
        modelData.forEach(System.out::println);
    }
}
