package ru.nucodelabs.data.ves;

import java.io.Serializable;

record ModelLayerImpl(
        double power,
        double resistance,
        boolean fixedPower,
        boolean fixedResistance
) implements ModelLayer, Serializable {
        @Override
        public double getPower() {
                return power;
        }

        @Override
        public double getResistance() {
                return resistance;
        }

        @Override
        public boolean isFixedPower() {
                return fixedPower;
        }

        @Override
        public boolean isFixedResistance() {
                return fixedResistance;
        }
}
