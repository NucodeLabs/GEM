package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Min;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.DEDUCTION,
        defaultImpl = ModelLayerImpl.class
)
public interface ModelLayer {

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
    @Min(0) double getResistance();
}
