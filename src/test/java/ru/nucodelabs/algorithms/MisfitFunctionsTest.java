package ru.nucodelabs.algorithms;

import org.junit.jupiter.api.Test;
import ru.nucodelabs.algorithms.charts.MisfitFunctions;

public class MisfitFunctionsTest {
    @Test
    void testJNI() {
        System.out.println("CalcRelativeDeviation(236, 240):");
        System.out.println(MisfitFunctions.calculateRelativeDeviation(236, 240));

        System.out.println("CalcRelativeDeviationWithError(236, 5, 240):");
        System.out.println(MisfitFunctions.calculateRelativeDeviationWithError(236, 5, 240));
    }
}
