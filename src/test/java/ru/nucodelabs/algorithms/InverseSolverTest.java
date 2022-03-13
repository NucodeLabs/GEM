package ru.nucodelabs.algorithms;

import org.apache.commons.math3.optim.PointValuePair;
import org.junit.jupiter.api.Test;
import ru.nucodelabs.ShiraPicket;
import ru.nucodelabs.algorithms.inverseSolver.InverseSolver;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.MODFile;
import ru.nucodelabs.files.sonet.STTFile;
import ru.nucodelabs.files.sonet.SonetImport;

import java.io.File;
import java.io.FileNotFoundException;

public class InverseSolverTest {
    @Test
    void inverseSolverTest_1() throws FileNotFoundException {
        Picket picket = ShiraPicket.getPicket();

        ModelData modelData = InverseSolver.getOptimizedPicket(picket);
    }
}
