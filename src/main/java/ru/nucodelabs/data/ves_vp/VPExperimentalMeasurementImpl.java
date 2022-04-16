package ru.nucodelabs.data.ves_vp;

import java.io.Serializable;

record VPExperimentalMeasurementImpl(
        double ab2,
        double mn2,
        double resistanceApparent,
        double errorResistanceApparent,
        double amperage,
        double voltage,
        double polarizationApparent,
        double errorPolarizationApparent
) implements VPExperimentalMeasurement, Serializable {
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
    public double geErrorResistanceApparent() {
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
    public double getPolarizationApparent() {
        return polarizationApparent();
    }

    @Override
    public double getErrorPolarizationApparent() {
        return errorPolarizationApparent();
    }
}