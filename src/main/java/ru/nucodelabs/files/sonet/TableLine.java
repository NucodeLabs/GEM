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
        this.resistivity = resistanceAppProperty();
        this.resistivityError = resistivityErrorProperty();
        this.polarization = polarizationProperty();
        this.polarizationError = polarizationErrorProperty();
    }

    //Range AB

    public double getAB_2() {
        return rangeAB.get();
    }

    public void setAB_2(double rangeAB) {
        this.rangeAB.set(rangeAB);
    }

    public SimpleDoubleProperty rangeABProperty() {
        if (rangeAB == null) {
            setAB_2(0.0);
        }
        return rangeAB;
    }

    //Range MN

    public double getMN_2() {
        return rangeMN.get();
    }

    public void setMN_2(double rangeMN) {
        this.rangeMN.set(rangeMN);
    }

    public SimpleDoubleProperty rangeMNProperty() {
        if (rangeMN == null) {
            setMN_2(0.0);
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

    public SimpleDoubleProperty resistanceAppProperty() {
        if (resistivity == null) {
            setResistanceApp(0.0);
        }
        return resistivity;
    }

    public double getResistanceApp() {
        return resistivity.get();
    }

    public void setResistanceApp(double resistivity) {
        this.resistivity.set(resistivity);
    }

    //Resistivity Error

    public double getErrorResistanceApp() {
        return resistivityError.get();
    }

    public void setErrorResistanceApp(double resistivityError) {
        this.resistivityError.set(resistivityError);
    }

    public SimpleDoubleProperty resistivityErrorProperty() {
        if (resistivityError == null) {
            setErrorResistanceApp(0.0);
        }
        return resistivityError;
    }

    //Polarization

    public double getPolarizationApp() {
        return polarization.get();
    }

    public void setPolarizationApp(double polarization) {
        this.polarization.set(polarization);
    }

    public SimpleDoubleProperty polarizationProperty() {
        if (polarization == null) {
            setPolarizationApp(0.0);
        }
        return polarization;
    }

    //Polarization Error

    public double getErrorPolarizationApp() {
        return polarizationError.get();
    }

    public void setErrorPolarizationApp(double polarizationError) {
        this.polarizationError.set(polarizationError);
    }

    public SimpleDoubleProperty polarizationErrorProperty() {
        if (polarizationError == null) {
            setErrorPolarizationApp(0.0);
        }
        return polarizationError;
    }
}
