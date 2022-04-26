package ru.nucodelabs.data.ves;

import java.io.Serializable;

record VPModelLayerImpl(
        double power,
        double resistance,
        double polarization
) implements VPModelLayer, Serializable {
    @Override
    public double getPower() {
        return power();
    }

    @Override
    public double getResistance() {
        return resistance();
    }

    @Override
    public double getPolarization() {
        return polarization();
    }
}
