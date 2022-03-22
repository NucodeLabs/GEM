package ru.nucodelabs.gem.view.tables;

import javafx.util.StringConverter;

public class Tables {
    /**
     * Returns {@code Double::parseDouble} or {@code NaN} if catch {@code NumberFormatException}
     */
    public static StringConverter<Double> doubleStringConverter = new StringConverter<>() {
        @Override
        public String toString(Double object) {
            return object.toString();
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
