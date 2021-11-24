package ru.nucodelabs.files.sonet;

import javafx.beans.property.SimpleDoubleProperty;

public class TableLine {

    private SimpleDoubleProperty rangeAB;
    private SimpleDoubleProperty rangeMN;
    private SimpleDoubleProperty voltage;
    private SimpleDoubleProperty amperage;
    private SimpleDoubleProperty resistivity;
    private SimpleDoubleProperty resistivityError;
    private SimpleDoubleProperty polarization;
    private SimpleDoubleProperty polarizationError;
    public TableLine() {
        this.rangeAB = new SimpleDoubleProperty();
        this.rangeMN = new SimpleDoubleProperty();
        this.amperage = new SimpleDoubleProperty();
        this.voltage = new SimpleDoubleProperty();
        this.resistivity = new SimpleDoubleProperty();
        this.resistivityError = new SimpleDoubleProperty();
        this.polarization = new SimpleDoubleProperty();
        this.polarizationError = new SimpleDoubleProperty();

        this.rangeAB = rangeABProperty();
        this.rangeMN = rangeMNProperty();
        this.amperage = amperageProperty();
        this.voltage = voltageProperty();
        this.resistivity = resistivityProperty();
        this.resistivityError = resistivityErrorProperty();
        this.polarization = polarizationProperty();
        this.polarizationError = polarizationErrorProperty();
    }

    //Range AB

    public double getRangeAB() {
        return rangeAB.get();
    }

    public void setRangeAB(double rangeAB) {
        this.rangeAB.set(rangeAB);
    }

    public SimpleDoubleProperty rangeABProperty() {
        if (rangeAB == null) {
            setRangeAB(0.0);
        }
        return rangeAB;
    }

    //Range MN

    public double getRangeMN() {
        return rangeMN.get();
    }

    public void setRangeMN(double rangeMN) {
        this.rangeMN.set(rangeMN);
    }

    public SimpleDoubleProperty rangeMNProperty() {
        if (rangeMN == null) {
            setRangeMN(0.0);
        }
        return rangeMN;
    }

    //Power

    public double getVoltage() {
        return voltage.get();
    }

    public void setVoltage(double voltage) {
        this.voltage.set(voltage);
    }

    public SimpleDoubleProperty voltageProperty() {
        if (voltage == null) {
            setVoltage(0.0);
        }
        return voltage;
    }

    //Amperage

    public double getAmperage() {
        return amperage.get();
    }

    public void setAmperage(double amperage) {
        this.amperage.set(amperage);
    }

    public SimpleDoubleProperty amperageProperty() {
        if (amperage == null) {
            setAmperage(0.0);
        }
        return amperage;
    }

    //Resistivity

    public SimpleDoubleProperty resistivityProperty() {
        if (resistivity == null) {
            setResistivity(0.0);
        }
        return resistivity;
    }

    public double getResistivity() {
        return resistivity.get();
    }

    public void setResistivity(double resistivity) {
        this.resistivity.set(resistivity);
    }

    //Resistivity Error

    public double getResistivityError() {
        return resistivityError.get();
    }

    public void setResistivityError(double resistivityError) {
        this.resistivityError.set(resistivityError);
    }

    public SimpleDoubleProperty resistivityErrorProperty() {
        if (resistivityError == null) {
            setResistivityError(0.0);
        }
        return resistivityError;
    }

    //Polarization

    public double getPolarization() {
        return polarization.get();
    }

    public void setPolarization(double polarization) {
        this.polarization.set(polarization);
    }

    public SimpleDoubleProperty polarizationProperty() {
        if (polarization == null) {
            setPolarization(0.0);
        }
        return polarization;
    }

    //Polarization Error

    public double getPolarizationError() {
        return polarizationError.get();
    }

    public void setPolarizationError(double polarizationError) {
        this.polarizationError.set(polarizationError);
    }

    public SimpleDoubleProperty polarizationErrorProperty() {
        if (polarizationError == null) {
            setPolarizationError(0.0);
        }
        return polarizationError;
    }
}
