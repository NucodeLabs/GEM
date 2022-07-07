package ru.nucodelabs.gem.app.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nucodelabs.algorithms.forward_solver.ForwardSolver;
import ru.nucodelabs.algorithms.inverse_solver.InverseSolver;

import java.util.Optional;
import java.util.UUID;

public class SectionManagerTest {
    @Test
    void test() {
        SectionManager sectionManager = new SectionManager(new InverseSolver(ForwardSolver.createDefault()));

        Assertions.assertEquals(Optional.empty(), sectionManager.indexById(UUID.randomUUID()));
    }
}
