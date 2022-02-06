package ru.nucodelabs.files.sonet.table_things;

import javafx.beans.property.SimpleDoubleProperty;

public class ModelTableCellProperties {
    private SimpleDoubleProperty resistanceApparent = new SimpleDoubleProperty();
    private SimpleDoubleProperty resistanceErrorApparent = new SimpleDoubleProperty();
    private SimpleDoubleProperty AB_2 = new SimpleDoubleProperty();
    private SimpleDoubleProperty MN_2 = new SimpleDoubleProperty();
    private SimpleDoubleProperty amperage = new SimpleDoubleProperty();
    private SimpleDoubleProperty voltage = new SimpleDoubleProperty();
    private SimpleDoubleProperty polarisation = new SimpleDoubleProperty();
    private SimpleDoubleProperty polarisationError = new SimpleDoubleProperty();

    /**Pack of setters ang getters for binding.
     */

    public double getResistanceApparent() {
        return resistanceApparent.get();
    }

    public SimpleDoubleProperty resistanceApparentProperty() {
        return resistanceApparent;
    }

    public void setResistanceApparent(double resistanceApparent) {
        this.resistanceApparent.set(resistanceApparent);
    }

    public double getResistanceErrorApparent() {
        return resistanceErrorApparent.get();
    }

    public SimpleDoubleProperty resistanceErrorApparentProperty() {
        return resistanceErrorApparent;
    }

    public void setResistanceErrorApparent(double resistanceErrorApparent) {
        this.resistanceErrorApparent.set(resistanceErrorApparent);
    }

    public double getAB_2() {
        return AB_2.get();
    }

    public SimpleDoubleProperty AB_2Property() {
        return AB_2;
    }

    public void setAB_2(double AB_2) {
        this.AB_2.set(AB_2);
    }

    public double getMN_2() {
        return MN_2.get();
    }

    public SimpleDoubleProperty MN_2Property() {
        return MN_2;
    }

    public void setMN_2(double MN_2) {
        this.MN_2.set(MN_2);
    }

    public double getAmperage() {
        return amperage.get();
    }

    public SimpleDoubleProperty amperageProperty() {
        return amperage;
    }

    public void setAmperage(double amperage) {
        this.amperage.set(amperage);
    }

    public double getVoltage() {
        return voltage.get();
    }

    public SimpleDoubleProperty voltageProperty() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage.set(voltage);
    }

    public double getPolarisation() {
        return polarisation.get();
    }

    public SimpleDoubleProperty polarisationProperty() {
        return polarisation;
    }

    public void setPolarisation(double polarisation) {
        this.polarisation.set(polarisation);
    }

    public double getPolarisationError() {
        return polarisationError.get();
    }

    public SimpleDoubleProperty polarisationErrorProperty() {
        return polarisationError;
    }

    public void setPolarisationError(double polarisationError) {
        this.polarisationError.set(polarisationError);
    }
}
