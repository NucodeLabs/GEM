package ru.nucodelabs.data.ves;

import jakarta.validation.constraints.Min;

public record ExperimentalDataRow(
        @Min(0) int index,
        @Min(0) double resistanceApparent,
        @Min(0) double ab_2,
        @Min(0) double mn_2,
        @Min(0) double errorResistanceApparent,
        @Min(0) double polarizationApparent,
        @Min(0) double errorPolarizationApparent,
        @Min(0) double amperage,
        @Min(0) double voltage
) {
    public int getIndex() {
        return index();
    }

    public double getResistanceApparent() {
        return resistanceApparent();
    }

    public double getAB_2() {
        return ab_2();
    }

    public double getMN_2() {
        return mn_2();
    }

    public double getErrorResistanceApparent() {
        return errorResistanceApparent();
    }

    public double getPolarizationApparent() {
        return polarizationApparent();
    }

    public double getErrorPolarizationApparent() {
        return errorPolarizationApparent();
    }

    public double getAmperage() {
        return amperage();
    }

    public double getVoltage() {
        return voltage();
    }
}
