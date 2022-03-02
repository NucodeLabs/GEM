package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.STTFile;

import java.util.List;

import static ru.nucodelabs.data.ves.Sizes.isEqualSizes;
import static ru.nucodelabs.data.ves.Sizes.minSize;

public record ExperimentalData(
        // AB/2, м
        List<Double> ab_2,
        // MN/2, м
        List<Double> mn_2,
        // Ток, мА
        List<Double> amperage,
        // Напряжение, мВ
        List<Double> voltage,
        // Сопротивление кажущееся, Ом * м
        List<Double> resistanceApparent,
        // Погрешность, %
        List<Double> errorResistanceApparent,
        // Поляризация кажущаяся, %
        List<Double> polarizationApparent,
        // Погрешность, %
        List<Double> errorPolarizationApparent
) {
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
    public boolean isUnsafe() {
        return !isEqualSizes(this, true);
    }

    @JsonIgnore
    public Integer getSize() {
        return minSize(this, true);
    }
}
