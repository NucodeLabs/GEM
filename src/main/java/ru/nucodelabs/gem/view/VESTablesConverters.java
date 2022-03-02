package ru.nucodelabs.gem.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.gem.view.usercontrols.vestables.tablelines.ExperimentalTableLine;
import ru.nucodelabs.gem.view.usercontrols.vestables.tablelines.ModelTableLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VESTablesConverters {

    private VESTablesConverters() {
    }

    public static ObservableList<ExperimentalTableLine> toExperimentalTableData(final ExperimentalData experimentalData) {
        List<ExperimentalTableLine> tableLines = new ArrayList<>();
        if (experimentalData != null) {
            for (int i = 0; i < experimentalData.getSize(); i++) {
                tableLines.add(
                        new ExperimentalTableLine(
                                Objects.requireNonNullElse(
                                        experimentalData.resistanceApparent().get(i), 0d),
                                Objects.requireNonNullElse(
                                        experimentalData.ab_2().get(i), 0d),
                                Objects.requireNonNullElse(
                                        experimentalData.mn_2().get(i), 0d),
                                Objects.requireNonNullElse(
                                        experimentalData.errorResistanceApparent().get(i), 0d),
                                Objects.requireNonNullElse(
                                        experimentalData.polarizationApparent().get(i), 0d),
                                Objects.requireNonNullElse(
                                        experimentalData.errorPolarizationApparent().get(i), 0d),
                                Objects.requireNonNullElse(
                                        experimentalData.amperage().get(i), 0d),
                                Objects.requireNonNullElse(
                                        experimentalData.voltage().get(i), 0d)
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
                                Objects.requireNonNullElse(
                                        modelData.resistance().get(i), 0d),
                                Objects.requireNonNullElse(
                                        modelData.power().get(i), 0d),
                                Objects.requireNonNullElse(
                                        modelData.polarization().get(i), 0d)
                        )
                );
            }
        }
        return FXCollections.observableArrayList(tableLines);
    }

}
