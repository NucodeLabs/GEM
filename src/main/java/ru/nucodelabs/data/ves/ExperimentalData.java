package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.DEDUCTION,
        defaultImpl = ExperimentalDataImpl.class
)
public interface ExperimentalData extends Serializable {

    static ExperimentalData create(
            double ab2,
            double mn2,
            double resistanceApparent,
            double errorResistanceApparent,
            double amperage,
            double voltage
    ) {
        return new ExperimentalDataImpl(
                ab2, mn2, resistanceApparent, errorResistanceApparent, amperage, voltage);
    }

    /**
     * AB/2, м
     */
    @Positive double getAb2();

    /**
     * MN/2, м
     */
    @Positive double getMn2();

    /**
     * Сопротивление кажущееся, Ом * м
     */
    @Positive double getResistanceApparent();

    /**
     * Погрешность, %
     */
    @Min(0) @Max(100) double getErrorResistanceApparent();

    /**
     * Ток, мА
     */
    @Min(0) double getAmperage();

    /**
     * Напряжение, мВ
     */
    @Min(0) double getVoltage();

    default ExperimentalData withAb2(double ab2) {
        if (ab2 == getAb2()) {
            return this;
        } else {
            return create(ab2, getMn2(), getResistanceApparent(), getErrorResistanceApparent(), getAmperage(), getVoltage());
        }
    }

    default ExperimentalData withMn2(double mn2) {
        if (mn2 == getMn2()) {
            return this;
        } else {
            return create(getAb2(), mn2, getResistanceApparent(), getErrorResistanceApparent(), getAmperage(), getVoltage());
        }
    }

    default ExperimentalData withResistanceApparent(double resistanceApparent) {
        if (resistanceApparent == getResistanceApparent()) {
            return this;
        } else {
            return create(getAb2(), getMn2(), resistanceApparent, getErrorResistanceApparent(), getAmperage(), getVoltage());
        }
    }

    default ExperimentalData withErrorResistanceApparent(double errorResistanceApparent) {
        if (errorResistanceApparent == getErrorResistanceApparent()) {
            return this;
        } else {
            return create(getAb2(), getMn2(), getResistanceApparent(), errorResistanceApparent, getAmperage(), getVoltage());
        }
    }

    default ExperimentalData withAmperage(double amperage) {
        if (amperage == getAmperage()) {
            return this;
        } else {
            return create(getAb2(), getMn2(), getResistanceApparent(), getErrorResistanceApparent(), amperage, getVoltage());
        }
    }

    default ExperimentalData withVoltage(double voltage) {
        if (voltage == getVoltage()) {
            return this;
        } else {
            return create(getAb2(), getMn2(), getResistanceApparent(), getErrorResistanceApparent(), getAmperage(), voltage);
        }
    }
}
