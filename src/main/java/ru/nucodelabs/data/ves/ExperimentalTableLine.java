package ru.nucodelabs.data.ves;

public record ExperimentalTableLine(
        Double resistanceApparent,
        Double ab_2,
        Double mn_2,
        Double errorResistanceApparent,
        Double polarizationApparent,
        Double errorPolarizationApparent,
        Double amperage,
        Double voltage
) {

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
