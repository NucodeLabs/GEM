package ru.nucodelabs.gem.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.gem.view.usercontrols.vestables.property_data.ExpTableLine;
import ru.nucodelabs.gem.view.usercontrols.vestables.property_data.ModelTableLine;

import java.util.ArrayList;
import java.util.List;

public class VESTablesConverters {

    public static ObservableList<ExpTableLine> toExperimentalTableData(final ExperimentalData expData) {
        List<ExpTableLine> tableLines = new ArrayList<>();
        if (expData != null) {
            for (int i = 0; i < expData.getSize(); i++) {
                tableLines.add(
                        new ExpTableLine(
                                expData.getResistanceApparent().get(i),
                                expData.getAB_2().get(i),
                                expData.getMN_2().get(i),
                                expData.getErrorResistanceApparent().get(i),
                                expData.getPolarizationApparent().get(i),
                                expData.getErrorPolarizationApparent().get(i),
                                expData.getAmperage().get(i),
                                expData.getVoltage().get(i)
                        )
                );
            }
        }
        return FXCollections.observableArrayList(tableLines);
    }

    public static ObservableList<ModelTableLine> toModelTableData(final ModelData modelData) {
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
        return FXCollections.observableArrayList(tableLines);
    }

}
