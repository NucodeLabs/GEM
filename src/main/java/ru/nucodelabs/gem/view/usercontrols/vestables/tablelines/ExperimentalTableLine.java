package ru.nucodelabs.gem.view.usercontrols.vestables.tablelines;

import javafx.beans.property.SimpleDoubleProperty;

public class ExperimentalTableLine {

    /**
     * Сопротивление экспериментальных данных
     */
    private final SimpleDoubleProperty resistanceApparent;
    /**
     * Разнос внешних электродов экспериментальных данных
     */
    private final SimpleDoubleProperty AB_2;
    /**
     * Разнос внутренних электродов экспериментальных данных
     */
    private final SimpleDoubleProperty MN_2;
    /**
     * Погрешность сопротивления экспериментальных данных
     */
    private final SimpleDoubleProperty errorResistanceApparent;
    /**
     * Поляризация экспериментальных данных
     */
    private final SimpleDoubleProperty polarizationApparent;
    /**
     * Погрешность поляризации экспериментальных данных
     */
    private final SimpleDoubleProperty errorPolarizationApparent;
    /**
     * Сила тока экспериментальных данных
     */
    private final SimpleDoubleProperty amperage;
    /**
     * Напряжение экспериментальных данных
     */
    private final SimpleDoubleProperty voltage;

    public ExperimentalTableLine(Double resistanceApparent,
                                 Double ab_2,
                                 Double mn_2,
                                 Double errorResistanceApparent,
                                 Double polarizationApparent,
                                 Double errorPolarizationApparent,
                                 Double amperage,
                                 Double voltage) {
        this.resistanceApparent = new SimpleDoubleProperty(resistanceApparent);
        this.errorResistanceApparent = new SimpleDoubleProperty(errorResistanceApparent);
        this.AB_2 = new SimpleDoubleProperty(ab_2);
        this.MN_2 = new SimpleDoubleProperty(mn_2);
        this.polarizationApparent = new SimpleDoubleProperty(polarizationApparent);
        this.errorPolarizationApparent = new SimpleDoubleProperty(errorPolarizationApparent);
        this.amperage = new SimpleDoubleProperty(amperage);
        this.voltage = new SimpleDoubleProperty(voltage);
    }

    public double getResistanceApparent() {
        return resistanceApparent.get();
    }

    public SimpleDoubleProperty resistanceApparentProperty() {
        return resistanceApparent;
    }

    public void setResistanceApparent(double resistanceApparent) {
        this.resistanceApparent.set(resistanceApparent);
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

    public double getErrorResistanceApparent() {
        return errorResistanceApparent.get();
    }

    public SimpleDoubleProperty errorResistanceApparentProperty() {
        return errorResistanceApparent;
    }

    public void setErrorResistanceApparent(double errorResistanceApparent) {
        this.errorResistanceApparent.set(errorResistanceApparent);
    }

    public double getPolarizationApparent() {
        return polarizationApparent.get();
    }

    public SimpleDoubleProperty polarizationApparentProperty() {
        return polarizationApparent;
    }

    public void setPolarizationApparent(double polarizationApparent) {
        this.polarizationApparent.set(polarizationApparent);
    }

    public double getErrorPolarizationApparent() {
        return errorPolarizationApparent.get();
    }

    public SimpleDoubleProperty errorPolarizationApparentProperty() {
        return errorPolarizationApparent;
    }

    public void setErrorPolarizationApparent(double errorPolarizationApparent) {
        this.errorPolarizationApparent.set(errorPolarizationApparent);
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
}
