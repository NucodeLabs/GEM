package ru.nucodelabs.gem;

import org.junit.jupiter.api.Test;
import ru.nucodelabs.gem.view.PowerOf10Formatter;

import java.util.Arrays;

public class PowerOf10FormatterTest {
    @Test
    void test() {
        Number[] src = new Number[]{1.23, 4.56, 7.089};
        Arrays.stream(src).forEach(n -> System.out.println((new PowerOf10Formatter()).toString(n)));
    }
}
