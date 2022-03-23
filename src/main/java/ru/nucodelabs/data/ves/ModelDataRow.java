package ru.nucodelabs.data.ves;

import jakarta.validation.constraints.Min;

public record ModelDataRow(
        @Min(0) int index,
        @Min(0) Double resistance,
        @Min(0) Double power,
        @Min(0) Double polarization
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
