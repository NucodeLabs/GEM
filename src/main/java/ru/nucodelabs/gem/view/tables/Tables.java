package ru.nucodelabs.gem.view.tables;

import javafx.beans.binding.Bindings;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
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

    private static final Callback<TableColumn<Object, Integer>, TableCell<Object, Integer>> INDEX_CELL_FACTORY = col -> {
        TableCell<Object, Integer> cell = new TableCell<>();
//        cell.textProperty().bind(Bindings.when(cell.emptyProperty())
//                .then("")
//                .otherwise(cell.indexProperty().asString()));

        cell.tableViewProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null && !cell.textProperty().isBound()) {
                        cell.textProperty().bind(Bindings.createStringBinding(
                                        () -> cell.isEmpty() ? "" : String.valueOf(cell.getIndex()),
                                        cell.getTableView().getItems(),
                                        cell.getTableView().itemsProperty(),
                                        cell.emptyProperty(),
                                        cell.indexProperty()
                                )
                        );
                    }
                }
        );
        return cell;
    };

    public static Callback<TableColumn<Object, Integer>, TableCell<Object, Integer>> indexCellFactory() {
        return INDEX_CELL_FACTORY;
    }

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
