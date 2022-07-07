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
            boolean fixedPower,
            boolean fixedResistance
    ) {
        return new ModelLayerImpl(power, resistance, fixedPower, fixedResistance);
    }

    static ModelLayer createNotFixed(
            double power,
            double resistance
    ) {
        return new ModelLayerImpl(power, resistance, false, false);
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
    boolean isFixedPower();

    boolean isFixedResistance();

    default ModelLayer withPower(double power) {
        if (power == getPower()) {
            return this;
        } else {
            return create(power, getResistance(), isFixedPower(), isFixedResistance());
        }
    }

    default ModelLayer withResistance(double resistance) {
        if (resistance == getResistance()) {
            return this;
        } else {
            return create(getPower(), resistance, isFixedPower(), isFixedResistance());
        }
    }

    default ModelLayer withFixedPower(boolean fixedPower) {
        if (fixedPower == isFixedPower()) {
            return this;
        }
        return create(getPower(), getResistance(), fixedPower, isFixedResistance());
    }

    default ModelLayer withFixedResistance(boolean fixedResistance) {
        if (fixedResistance == isFixedResistance()) {
            return this;
        }
        return create(getPower(), getResistance(), isFixedPower(), fixedResistance);
    }
}
