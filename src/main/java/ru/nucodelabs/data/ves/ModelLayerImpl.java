package ru.nucodelabs.data.ves;

import java.io.Serializable;

record ModelLayerImpl(
        double resistance,
        double power
) implements ModelLayer, Serializable {
        @Override
        public double getPower() {
                return power();
        }

        @Override
        public double getResistance() {
                return resistance();
        }
}
