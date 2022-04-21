package ru.nucodelabs.data.ves_vp;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import ru.nucodelabs.data.ves.ExperimentalData;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.DEDUCTION,
        defaultImpl = VPExperimentalDataImpl.class
)
public interface VPExperimentalData extends ExperimentalData {

    static VPExperimentalData create(
            double ab2,
            double mn2,
            double resistanceApparent,
            double errorResistanceApparent,
            double amperage,
            double voltage,
            double polarizationApparent,
            double errorPolarizationApparent
    ) {
        return new VPExperimentalDataImpl(
                ab2, mn2, resistanceApparent, errorResistanceApparent, amperage, voltage,
                polarizationApparent, errorPolarizationApparent);
    }

    /**
     * Поляризация кажущаяся, %
     */
    @Min(0) double getPolarizationApparent();

    /**
     * Погрешность, %
     */
    @Min(0) @Max(100) double getErrorPolarizationApparent();

    default VPExperimentalData withPolarizationApparent(double polarizationApparent) {
        if (polarizationApparent == getPolarizationApparent()) {
            return this;
        } else {
            return create(getAb2(), getMn2(), getResistanceApparent(), getErrorResistanceApparent(), getAmperage(), getVoltage(), polarizationApparent, getErrorPolarizationApparent());
        }
    }

    default VPExperimentalData withErrorPolarizationApparent(double errorPolarizationApparent) {
        if (errorPolarizationApparent == getErrorPolarizationApparent()) {
            return this;
        } else {
            return create(getAb2(), getMn2(), getResistanceApparent(), getErrorResistanceApparent(), getAmperage(), getVoltage(), getPolarizationApparent(), errorPolarizationApparent);
        }
    }
}
