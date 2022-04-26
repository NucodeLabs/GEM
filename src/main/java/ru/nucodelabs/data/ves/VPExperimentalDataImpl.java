package ru.nucodelabs.data.ves;

import java.io.Serializable;

record VPExperimentalDataImpl(
        double ab2,
        double mn2,
        double resistanceApparent,
        double errorResistanceApparent,
        double amperage,
        double voltage,
        double polarizationApparent,
        double errorPolarizationApparent
) implements VPExperimentalData, Serializable {
    @Override
    public double getAb2() {
        return ab2();
    }

    @Override
    public double getMn2() {
        return mn2();
    }

    @Override
    public double getResistanceApparent() {
        return resistanceApparent();
    }

    @Override
    public double getErrorResistanceApparent() {
        return errorResistanceApparent();
    }

    @Override
    public double getAmperage() {
        return amperage();
    }

    @Override
    public double getVoltage() {
        return voltage();
    }

    @Override
    public VPExperimentalData recalculateResistanceApparent() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public double getPolarizationApparent() {
        return polarizationApparent();
    }

    @Override
    public double getErrorPolarizationApparent() {
        return errorPolarizationApparent();
    }
}
