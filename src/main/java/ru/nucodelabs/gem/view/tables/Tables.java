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
}
