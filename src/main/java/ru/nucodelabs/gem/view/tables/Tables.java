package ru.nucodelabs.gem.view.tables;

import javafx.beans.binding.Bindings;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.text.DecimalFormat;
import java.text.ParseException;

final class Tables {

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
    static boolean validateDoubleInput(String s, DecimalFormat decimalFormat) {
        if (s.isBlank()) {
            return true;
        }
        try {
            decimalFormat.parse(s).doubleValue();
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
