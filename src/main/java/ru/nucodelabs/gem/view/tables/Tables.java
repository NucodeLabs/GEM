package ru.nucodelabs.gem.view.tables;

import javafx.util.StringConverter;

import java.math.RoundingMode;
import java.text.DecimalFormat;

final class Tables {

    private final static DecimalFormat DECIMAL_FORMAT;
    private final static StringConverter<Double> CONVERTER = new StringConverter<>() {
        @Override
        public String toString(Double object) {
            return decimalFormat().format(object);
        }

        @Override
        public Double fromString(String string) {
            try {
                return Double.parseDouble(string);
            } catch (NumberFormatException e) {
                return Double.NaN;
            }
        }
    };

    static {
        DECIMAL_FORMAT = new DecimalFormat("#.##");
        DECIMAL_FORMAT.setRoundingMode(RoundingMode.HALF_UP);
    }

    /**
     * Returns {@code Double::parseDouble} or {@code NaN} if catch {@code NumberFormatException}
     * Rounds to 2 decimal places
     */
    static StringConverter<Double> doubleStringConverter() {
        return CONVERTER;
    }

    static DecimalFormat decimalFormat() {
        return DECIMAL_FORMAT;
    }


    private Tables() {
    }

    /**
     * Validating string containing index in array
     *
     * @param s string
     * @return true if string represents valid index
     */
    static boolean validateIndexInput(String s) {
        if (s.isBlank()) {
            return true;
        }
        try {
            int val = Integer.parseInt(s);
            return val >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validating string containing non-negative double value
     *
     * @param s string
     * @return true if string represents valid non-negative double value
     */
    static boolean validateDataInput(String s) {
        if (s.isBlank()) {
            return true;
        }
        try {
            double val = Double.parseDouble(s);
            return !(val < 0);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
