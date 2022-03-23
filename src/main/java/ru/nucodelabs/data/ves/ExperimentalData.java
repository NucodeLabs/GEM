package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.STTFile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNullElse;

public record ExperimentalData(
        // AB/2, м
        @NotNull List<@Min(0) Double> ab_2,
        // MN/2, м
        @NotNull List<@Min(0) Double> mn_2,
        // Ток, мА
        @NotNull List<@Min(0) Double> amperage,
        // Напряжение, мВ
        @NotNull List<@Min(0) Double> voltage,
        // Сопротивление кажущееся, Ом * м
        @NotNull List<@Min(0) Double> resistanceApparent,
        // Погрешность, %
        @NotNull List<@Min(0) Double> errorResistanceApparent,
        // Поляризация кажущаяся, %
        @NotNull List<@Min(0) Double> polarizationApparent,
        // Погрешность, %
        @NotNull List<@Min(0) Double> errorPolarizationApparent
) implements Sizeable, Serializable {
    public static ExperimentalData from(STTFile sttFile, EXPFile expFile) {
        return new ExperimentalData(
                sttFile.getAB_2(),
                sttFile.getMN_2(),
                expFile.getAmperage(),
                expFile.getVoltage(),
                expFile.getResistanceApparent(),
                expFile.getErrorResistanceApparent(),
                expFile.getPolarizationApparent(),
                expFile.getErrorPolarizationApparent()
        );
    }

    public static ExperimentalData empty() {
        return new ExperimentalData(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    @JsonIgnore
    public List<ExperimentalDataRow> getRows() {
        List<ExperimentalDataRow> res = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            res.add(
                    new ExperimentalDataRow(
                            i,
                            requireNonNullElse(resistanceApparent().get(i), 0d),
                            requireNonNullElse(ab_2().get(i), 0d),
                            requireNonNullElse(mn_2().get(i), 0d),
                            requireNonNullElse(errorResistanceApparent().get(i), 0d),
                            requireNonNullElse(polarizationApparent().get(i), 0d),
                            requireNonNullElse(errorPolarizationApparent().get(i), 0d),
                            requireNonNullElse(amperage().get(i), 0d),
                            requireNonNullElse(voltage().get(i), 0d)
                    )
            );
        }
        return res;
    }
}
