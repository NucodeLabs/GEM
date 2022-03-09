package ru.nucodelabs.data.ves;

public record ModelTableLine(
        Double resistance,
        Double power,
        Double polarization
) {

    public double getResistance() {
        return resistance();
    }

    public double getPower() {
        return power();
    }

    public double getPolarization() {
        return polarization();
    }
}
