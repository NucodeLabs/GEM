package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.STTFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNullElse;

public record ExperimentalData(
        // AB/2, м
        @NotNull List<@Positive Double> ab_2,
        // MN/2, м
        @NotNull List<@Positive Double> mn_2,
        // Ток, мА
        @NotNull List<@Positive Double> amperage,
        // Напряжение, мВ
        @NotNull List<@Positive Double> voltage,
        // Сопротивление кажущееся, Ом * м
        @NotNull List<@Positive Double> resistanceApparent,
        // Погрешность, %
        @NotNull List<@Positive Double> errorResistanceApparent,
        // Поляризация кажущаяся, %
        @NotNull List<@Positive Double> polarizationApparent,
        // Погрешность, %
        @NotNull List<@Positive Double> errorPolarizationApparent
) implements Sizeable {
    public static ExperimentalData of(STTFile sttFile, EXPFile expFile) {
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

    @JsonIgnore
    public List<ExperimentalTableLine> getLines() {
        List<ExperimentalTableLine> res = new ArrayList<>();
        for (int i = 0; i < getSize(); i++) {
            res.add(
                    new ExperimentalTableLine(
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
