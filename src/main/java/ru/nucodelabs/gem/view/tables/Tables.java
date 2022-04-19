package ru.nucodelabs.gem.view.tables;

import javafx.beans.binding.Bindings;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

final class Tables {

    private final static DecimalFormat DECIMAL_FORMAT;
    private final static StringConverter<Double> CONVERTER = new StringConverter<>() {
        @Override
        public String toString(Double object) {
            try {
                return decimalFormat().format(object);
            } catch (IllegalArgumentException e) {
                return "";
            }
        }

        @Override
        public Double fromString(String string) {
            try {
                return decimalFormat().parse(string).doubleValue();
            } catch (ParseException e) {
                return Double.NaN;
            }
        }
    };

    private static final Callback<TableColumn<Object, Integer>, TableCell<Object, Integer>> INDEX_CELL_FACTORY = col -> {
        TableCell<Object, Integer> cell = new TableCell<>();

        cell.textProperty().bind(Bindings.when(cell.emptyProperty())
                .then("")
                .otherwise(cell.indexProperty().asString()));

        return cell;
    };

    public static Callback<TableColumn<Object, Integer>, TableCell<Object, Integer>> indexCellFactory() {
        return INDEX_CELL_FACTORY;
    }

    static {
        DECIMAL_FORMAT = new DecimalFormat();
        DECIMAL_FORMAT.setRoundingMode(RoundingMode.HALF_UP);
        DECIMAL_FORMAT.setMaximumFractionDigits(2);
        DECIMAL_FORMAT.setGroupingSize(3);
        var dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator(',');
        dfs.setGroupingSeparator(' ');
        DECIMAL_FORMAT.setDecimalFormatSymbols(dfs);
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
     * Validating string containing double value
     *
     * @param s string
     * @return true if string represents valid double value
     */
    static boolean validateDoubleInput(String s) {
        if (s.isBlank()) {
            return true;
        }
        try {
            decimalFormat().parse(s).doubleValue();
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
