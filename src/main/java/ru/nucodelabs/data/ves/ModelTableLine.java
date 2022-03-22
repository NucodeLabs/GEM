package ru.nucodelabs.data.ves;

public record ModelTableLine(
        int index,
        Double resistance,
        Double power,
        Double polarization
) {

    public int getIndex() {
        return index();
    }

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
