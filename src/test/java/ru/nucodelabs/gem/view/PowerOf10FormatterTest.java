package ru.nucodelabs.gem.view;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class PowerOf10FormatterTest {
    @Test
    void test() {
        Number[] src = new Number[]{1.23, 4.56, 7.089, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Arrays.stream(src).forEach(n -> System.out.println((new PowerOf10Formatter()).toString(n)));
    }
}
