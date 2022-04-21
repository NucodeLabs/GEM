package ru.nucodelabs.gem.app.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

public class SectionManagerTest {
    @Test
    void test() {
        SectionManager sectionManager = new SectionManager();

        Assertions.assertEquals(Optional.empty(), sectionManager.indexById(UUID.randomUUID()));
    }
}
