package ru.nucodelabs.data.ves;

import java.io.Serializable;

record ModelLayerImpl(
        double resistance,
        double power,
        boolean fixed
) implements ModelLayer, Serializable {
        @Override
        public double getPower() {
                return power();
        }

        @Override
        public double getResistance() {
                return resistance();
        }

        @Override
        public boolean isFixed() {
                return fixed();
        }
}
