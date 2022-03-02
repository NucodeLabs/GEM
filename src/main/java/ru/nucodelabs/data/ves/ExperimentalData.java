package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.STTFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private List<Integer> sizesList() {
        List<Integer> sizes = new ArrayList<>();
        if (ab_2.size() > 0) sizes.add(ab_2.size());
        if (mn_2.size() > 0) sizes.add(mn_2.size());
        if (amperage.size() > 0) sizes.add(amperage.size());
        if (voltage.size() > 0) sizes.add(voltage.size());
        if (resistanceApparent.size() > 0) sizes.add(resistanceApparent.size());
        if (errorResistanceApparent.size() > 0) sizes.add(errorPolarizationApparent.size());
        if (polarizationApparent.size() > 0) sizes.add(polarizationApparent.size());
        if (errorPolarizationApparent.size() > 0) sizes.add(errorPolarizationApparent.size());

        return sizes;
    }

    @JsonIgnore
    public boolean isUnsafe() {
        return sizesList().stream().distinct().count() != 1;
    }

    @JsonIgnore
    public Integer getSize() {
        return Collections.min(sizesList());
    }
}
