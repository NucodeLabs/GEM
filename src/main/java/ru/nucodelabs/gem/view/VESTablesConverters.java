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
                                        experimentalData.getResistanceApparent().get(i), 0d),
                                Objects.requireNonNullElse(
                                        experimentalData.getAB_2().get(i), 0d),
                                Objects.requireNonNullElse(
                                        experimentalData.getMN_2().get(i), 0d),
                                Objects.requireNonNullElse(
                                        experimentalData.getErrorResistanceApparent().get(i), 0d),
                                Objects.requireNonNullElse(
                                        experimentalData.getPolarizationApparent().get(i), 0d),
                                Objects.requireNonNullElse(
                                        experimentalData.getErrorPolarizationApparent().get(i), 0d),
                                Objects.requireNonNullElse(
                                        experimentalData.getAmperage().get(i), 0d),
                                Objects.requireNonNullElse(
                                        experimentalData.getVoltage().get(i), 0d)
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
                                        modelData.getResistance().get(i), 0d),
                                Objects.requireNonNullElse(
                                        modelData.getPower().get(i), 0d),
                                Objects.requireNonNullElse(
                                        modelData.getPolarization().get(i), 0d)
                        )
                );
            }
        }
        return FXCollections.observableArrayList(tableLines);
    }

}
