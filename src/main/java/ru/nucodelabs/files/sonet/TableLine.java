package ru.nucodelabs.files.sonet;

import javafx.beans.property.SimpleDoubleProperty;

public class TableLine {

    private SimpleDoubleProperty AB_2;
    private SimpleDoubleProperty MN_2;
    private SimpleDoubleProperty voltage;
    private SimpleDoubleProperty amperage;
    private SimpleDoubleProperty resistanceApparent;
    private SimpleDoubleProperty errorResistanceApparent;
    private SimpleDoubleProperty polarizationApparent;
    private SimpleDoubleProperty errorPolarizationApparent;

    public TableLine() {
        this.AB_2 = new SimpleDoubleProperty();
        this.MN_2 = new SimpleDoubleProperty();
        this.amperage = new SimpleDoubleProperty();
        this.voltage = new SimpleDoubleProperty();
        this.resistanceApparent = new SimpleDoubleProperty();
        this.errorResistanceApparent = new SimpleDoubleProperty();
        this.polarizationApparent = new SimpleDoubleProperty();
        this.errorPolarizationApparent = new SimpleDoubleProperty();

        this.AB_2 = AB_2Property();
        this.MN_2 = MN_2Property();
        this.amperage = amperageProperty();
        this.voltage = voltageProperty();
        this.resistanceApparent = resistanceApparentProperty();
        this.errorResistanceApparent = errorResistanceApparentProperty();
        this.polarizationApparent = polarizationApparentProperty();
        this.errorPolarizationApparent = errorPolarizationApparentProperty();
    }

    //Range AB

    public double getAB_2() {
        return AB_2.get();
    }

    public void setAB_2(double rangeAB) {
        this.AB_2.set(rangeAB);
    }

    public SimpleDoubleProperty AB_2Property() {
        if (AB_2 == null) {
            setAB_2(0.0);
        }
        return AB_2;
    }

    //Range MN

    public double getMN_2() {
        return MN_2.get();
    }

    public void setMN_2(double rangeMN) {
        this.MN_2.set(rangeMN);
    }

    public SimpleDoubleProperty MN_2Property() {
        if (MN_2 == null) {
            setMN_2(0.0);
        }
        return MN_2;
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

    public SimpleDoubleProperty resistanceApparentProperty() {
        if (resistanceApparent == null) {
            setResistanceApparent(0.0);
        }
        return resistanceApparent;
    }

    public double getResistanceApparent() {
        return resistanceApparent.get();
    }

    public void setResistanceApparent(double newResistanceApparent) {
        this.resistanceApparent.set(newResistanceApparent);
    }

    //Resistance Error

    public double getErrorResistanceApp() {
        return errorResistanceApparent.get();
    }

    public void setErrorResistanceApp(double newErrorResistanceApparent) {
        this.errorResistanceApparent.set(newErrorResistanceApparent);
    }

    public SimpleDoubleProperty errorResistanceApparentProperty() {
        if (errorResistanceApparent == null) {
            setErrorResistanceApp(0.0);
        }
        return errorResistanceApparent;
    }

    //Polarization

    public double getPolarizationApp() {
        return polarizationApparent.get();
    }

    public void setPolarizationApp(double polarization) {
        this.polarizationApparent.set(polarization);
    }

    public SimpleDoubleProperty polarizationApparentProperty() {
        if (polarizationApparent == null) {
            setPolarizationApp(0.0);
        }
        return polarizationApparent;
    }

    //Polarization Error

    public double getErrorPolarizationApp() {
        return errorPolarizationApparent.get();
    }

    public void setErrorPolarizationApp(double polarizationError) {
        this.errorPolarizationApparent.set(polarizationError);
    }

    public SimpleDoubleProperty errorPolarizationApparentProperty() {
        if (errorPolarizationApparent == null) {
            setErrorPolarizationApp(0.0);
        }
        return errorPolarizationApparent;
    }
}
