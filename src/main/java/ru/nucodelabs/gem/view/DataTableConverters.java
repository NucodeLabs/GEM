package ru.nucodelabs.gem.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.gem.view.usercontrols.tables.property_data.ExpTableLine;
import ru.nucodelabs.gem.view.usercontrols.tables.property_data.ModelTableLine;

import java.util.ArrayList;
import java.util.List;

public class DataTableConverters {

    public static ObservableList<ExpTableLine> expDataToExpObservableTableLines(ExperimentalData expData) {
        return expTableLinesToExpObservableList(expDataToExpTableLines(expData));
    }

    public static ObservableList<ModelTableLine> modelDataToModelObservableTableLines(ModelData modelData) {
        return modTableLinesToModObservableList(modelDataToModelTableLines(modelData));
    }

    private static List<ExpTableLine> expDataToExpTableLines(ExperimentalData expData) {
        List<ExpTableLine> tableLines = new ArrayList<>();
        if (expData != null) {
            for (int i = 0; i < expData.getSize(); i++) {
                tableLines.add(
                        new ExpTableLine(
                                expData.getResistanceApparent().get(i),
                                expData.getAB_2().get(i),
                                expData.getMN_2().get(i),
                                expData.getErrorResistanceApparent().get(i),
                                0.0,//expData.getPolarizationApparent().get(i),
                                0.0,//expData.getErrorPolarizationApparent().get(i),
                                expData.getAmperage().get(i),
                                expData.getVoltage().get(i)
                        )
                );
            }
        }
        return tableLines;
    }

    private static List<ModelTableLine> modelDataToModelTableLines(ModelData modelData) {
        List<ModelTableLine> tableLines = new ArrayList<>();
        if (modelData != null) {
            for (int i = 0; i < modelData.getSize(); i++) {
                tableLines.add(
                        new ModelTableLine(
                                modelData.getResistance().get(i),
                                modelData.getPower().get(i),
                                modelData.getPolarization().get(i)
                        )
                );
            }
        }
        return tableLines;
    }

    private static ObservableList<ExpTableLine> expTableLinesToExpObservableList(List<ExpTableLine> tableLines) {
        return FXCollections.observableArrayList(tableLines);
    }

    private static ObservableList<ModelTableLine> modTableLinesToModObservableList(List<ModelTableLine> tableLines) {
        return FXCollections.observableArrayList(tableLines);
    }
}
