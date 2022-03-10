package ru.nucodelabs.data.ves;

public record ExperimentalTableLine(
        int index,
        double resistanceApparent,
        double ab_2,
        double mn_2,
        double errorResistanceApparent,
        double polarizationApparent,
        double errorPolarizationApparent,
        double amperage,
        double voltage
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
