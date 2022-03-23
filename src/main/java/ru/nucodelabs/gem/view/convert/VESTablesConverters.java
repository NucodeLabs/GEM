package ru.nucodelabs.gem.view.convert;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ExperimentalDataRow;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.ModelDataRow;

public class VESTablesConverters {

    private VESTablesConverters() {
    }

    public static ObservableList<ExperimentalDataRow> toExperimentalTableData(final ExperimentalData experimentalData) {
        if (experimentalData != null) {
            return FXCollections.observableList(experimentalData.getRows());
        } else {
            return FXCollections.emptyObservableList();
        }
    }

    public static ObservableList<ModelDataRow> toModelTableData(final ModelData modelData) {
        if (modelData != null) {
            return FXCollections.observableList(modelData.getRows());
        } else {
            return FXCollections.emptyObservableList();
        }
    }

}
