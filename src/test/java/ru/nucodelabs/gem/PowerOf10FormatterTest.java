package ru.nucodelabs.gem;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class PowerOf10FormatterTest {
    @Test
    void test() {
        Number[] src = new Number[]{1.23, 4.56, 7.089};
        Arrays.stream(src).forEach(n -> System.out.println(AppController.powerOf10Formatter.toString(n)));
    }
}
