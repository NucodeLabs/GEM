package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Min;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.DEDUCTION,
        defaultImpl = ModelLayerImpl.class
)
public sealed interface ModelLayer extends Serializable permits ModelLayerImpl, VPModelLayer {

    static ModelLayer create(
            double power,
            double resistance
    ) {
        return new ModelLayerImpl(resistance, power);
    }

    /**
     * Мощность, м
     */
    @Min(0) double getPower();

    /**
     * Сопротивление, Ом * м
     */
    @Min(1) double getResistance();

    default ModelLayer withPower(double power) {
        if (power == getPower()) {
            return this;
        } else {
            return create(power, getResistance());
        }
    }

    default ModelLayer withResistance(double resistance) {
        if (resistance == getResistance()) {
            return this;
        } else {
            return create(getPower(), resistance);
        }
    }
}
