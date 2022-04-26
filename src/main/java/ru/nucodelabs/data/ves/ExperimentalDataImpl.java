package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

record ExperimentalDataImpl(
        double ab2,
        double mn2,
        double resistanceApparent,
        double errorResistanceApparent,
        double amperage,
        double voltage
) implements ExperimentalData, Serializable {

    @JsonCreator
    static ExperimentalDataImpl create(
            @JsonProperty("ab2") Double ab2,
            @JsonProperty("mn2") Double mn2,
            @JsonProperty("resistanceApparent") Double resistanceApparent,
            @JsonProperty("errorResistanceApparent") Double errorResistanceApparent,
            @JsonProperty("amperage") Double amperage,
            @JsonProperty("voltage") Double voltage
    ) {
        return new ExperimentalDataImpl(
                ab2,
                mn2,
                Objects.requireNonNullElse(resistanceApparent, VesUtils.resistanceApparent(ab2, mn2, amperage, voltage)),
                Objects.requireNonNullElse(errorResistanceApparent, DEFAULT_ERROR),
                Objects.requireNonNull(amperage),
                Objects.requireNonNull(voltage)
        );
    }


    @JsonAlias({"ab2", "AB2", "AB/2"})
    @Override
    public double getAb2() {
        return ab2();
    }

    @JsonAlias({"mn2", "MN2", "MN/2"})
    @Override
    public double getMn2() {
        return mn2();
    }

    @JsonAlias({"resistanceApparent", "Ro_a"})
    @Override
    public double getResistanceApparent() {
        return resistanceApparent();
    }

    @JsonAlias({"errorResistanceApparent", "error"})
    @Override
    public double getErrorResistanceApparent() {
        return errorResistanceApparent();
    }

    @JsonAlias({"amperage", "I"})
    @Override
    public double getAmperage() {
        return amperage();
    }

    @JsonAlias({"voltage", "U"})
    @Override
    public double getVoltage() {
        return voltage();
    }

    @Override
    public ExperimentalData recalculateResistanceApparent() {
        return new ExperimentalDataImpl(ab2, mn2, VesUtils.resistanceApparent(ab2, mn2, amperage, voltage), errorResistanceApparent, amperage, voltage);
    }
}
