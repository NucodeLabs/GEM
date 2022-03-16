package ru.nucodelabs.gem.view.convert;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ExperimentalTableLine;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.ModelTableLine;

public class VESTablesConverters {

    private VESTablesConverters() {
    }

    public static ObservableList<ExperimentalTableLine> toExperimentalTableData(final ExperimentalData experimentalData) {
        if (experimentalData != null) {
            return FXCollections.observableList(experimentalData.getLines());
        } else {
            return FXCollections.emptyObservableList();
        }
    }

    public static ObservableList<ModelTableLine> toModelTableData(final ModelData modelData) {
        if (modelData != null) {
            return FXCollections.observableList(modelData.getLines());
        } else {
            return FXCollections.emptyObservableList();
        }
    }

}
