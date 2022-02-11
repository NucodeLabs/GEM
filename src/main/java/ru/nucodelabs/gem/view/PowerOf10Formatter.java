package ru.nucodelabs.gem.view;

import javafx.util.StringConverter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Formats number to ten with power of this number, only if its integer
 */
public class PowerOf10Formatter extends StringConverter<Number> {

    private static String toUpperIndex(String doubleString) {
        ArrayList<Character> resChars = new ArrayList<>();
        for (int i = 0; i < doubleString.length(); i++) {
            char c = doubleString.charAt(i);
            switch (c) {
                case '1' -> resChars.add('¹');
                case '2' -> resChars.add('²');
                case '3' -> resChars.add('³');
                case '4' -> resChars.add('⁴');
                case '5' -> resChars.add('⁵');
                case '6' -> resChars.add('⁶');
                case '7' -> resChars.add('⁷');
                case '8' -> resChars.add('⁸');
                case '9' -> resChars.add('⁹');
                case '0' -> resChars.add('⁰');
                case '.' -> resChars.add('\u0387');
                case '-' -> resChars.add('\u207b');
                default -> resChars.add(c);
            }
        }
        return resChars
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    @Override
    public String toString(Number object) {
        DecimalFormat format = new DecimalFormat();
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setDecimalSeparator('.');
        format.setDecimalFormatSymbols(formatSymbols);
        if (object.doubleValue() == 0) {
            return "1";
        } else if (object.doubleValue() - Math.ceil(object.doubleValue()) == 0) {
            return ("10" + toUpperIndex(format.format(object.doubleValue())));
        } else {
            return "";
        }
    }

    @Override
    public Number fromString(String string) {
        return null;
    }
}
