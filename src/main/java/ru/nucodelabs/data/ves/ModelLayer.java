package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Min;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.DEDUCTION,
        defaultImpl = ModelLayerImpl.class
)
public interface ModelLayer extends Serializable {

    static ModelLayer create(
            double power,
            double resistance,
            boolean fixed
    ) {
        return new ModelLayerImpl(resistance, power, fixed);
    }

    static ModelLayer create(
            double power,
            double resistance
    ) {
        return new ModelLayerImpl(resistance, power, false);
    }

    /**
     * Мощность, м
     */
    @Min(0) double getPower();

    /**
     * Сопротивление, Ом * м
     */
    @Min(1) double getResistance();

    /**
     * True if layer is fixed
     */
    boolean isFixed();

    default ModelLayer withPower(double power) {
        if (power == getPower()) {
            return this;
        } else {
            return create(power, getResistance(), isFixed());
        }
    }

    default ModelLayer withResistance(double resistance) {
        if (resistance == getResistance()) {
            return this;
        } else {
            return create(getPower(), resistance, isFixed());
        }
    }

    default ModelLayer withFixed(boolean fixed) {
        if (fixed == isFixed()) {
            return this;
        }
        return create(getPower(), getResistance(), fixed);
    }
}
