package ru.nucodelabs.gem.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.gem.view.usercontrols.vestables.tablelines.ExperimentalTableLine;
import ru.nucodelabs.gem.view.usercontrols.vestables.tablelines.ModelTableLine;

import java.util.ArrayList;
import java.util.List;

public class VESTablesConverters {

    public static ObservableList<ExperimentalTableLine> toExperimentalTableData(final ExperimentalData experimentalData) {
        List<ExperimentalTableLine> tableLines = new ArrayList<>();
        if (experimentalData != null) {
            for (int i = 0; i < experimentalData.getSize(); i++) {
                tableLines.add(
                        new ExperimentalTableLine(
                                experimentalData.getResistanceApparent().get(i),
                                experimentalData.getAB_2().get(i),
                                experimentalData.getMN_2().get(i),
                                experimentalData.getErrorResistanceApparent().get(i),
                                experimentalData.getPolarizationApparent().get(i),
                                experimentalData.getErrorPolarizationApparent().get(i),
                                experimentalData.getAmperage().get(i),
                                experimentalData.getVoltage().get(i)
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
