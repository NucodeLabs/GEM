package ru.nucodelabs.gem.view.usercontrols.vestables.tablelines;

import javafx.beans.property.SimpleDoubleProperty;

public class ExperimentalTableLine {

    /**
     * Сопротивление экспериментальных данных
     */
    private final SimpleDoubleProperty expResistance = new SimpleDoubleProperty();
    /**
     * Разнос внешних электродов экспериментальных данных
     */
    private final SimpleDoubleProperty expAB_2 = new SimpleDoubleProperty(0);
    /**
     * Разнос внутренних электродов экспериментальных данных
     */
    private final SimpleDoubleProperty expMN_2 = new SimpleDoubleProperty(0);
    /**
     * Погрешность сопротивления экспериментальных данных
     */
    private final SimpleDoubleProperty expErrorResistance = new SimpleDoubleProperty(0);
    /**
     * Поляризация экспериментальных данных
     */
    private final SimpleDoubleProperty expPolarisation = new SimpleDoubleProperty(0);
    /**
     * Погрешность поляризации экспериментальных данных
     */
    private final SimpleDoubleProperty expErrorPolarisation = new SimpleDoubleProperty(0);
    /**
     * Сила тока экспериментальных данных
     */
    private final SimpleDoubleProperty expAmperage = new SimpleDoubleProperty(0);
    /**
     * Напряжение экспериментальных данных
     */
    private final SimpleDoubleProperty expVoltage = new SimpleDoubleProperty(0);

       /*
    this.expAB_2 = new SimpleDoubleProperty(ab_2);
        this.expMN_2 = new SimpleDoubleProperty(mn_2);
        this.expResistance = new SimpleDoubleProperty(resistance);
        this.expErrorResistance = new SimpleDoubleProperty(errorResistance);
        this.expPolarisation= new SimpleDoubleProperty(polarization);
        this.expErrorPolarisation= new SimpleDoubleProperty(errorPolarization);
        this.expAmperage = new SimpleDoubleProperty(amperage);
        this.expVoltage = new SimpleDoubleProperty(voltage);
     */

    /*public ExpTableLine() {
        setExpResistance(0);
        setExpAB_2(0);
        setExpMN_2(0);
        setExpErrorResistance(0);
        setExpPolarisation(0);
        setExpErrorPolarisation(0);
        setExpAmperage(0);
        setExpVoltage(0);
    }*/

    public ExperimentalTableLine(Double resistance,
                                 Double ab_2,
                                 Double mn_2,
                                 Double errorResistance,
                                 Double polarization,
                                 Double errorPolarization,
                                 Double amperage,
                                 Double voltage) {
        setExpResistance(resistance);
        setExpAB_2(ab_2);
        setExpMN_2(mn_2);
        setExpErrorResistance(errorResistance);
        setExpPolarisation(polarization);
        setExpErrorPolarisation(errorPolarization);
        setExpAmperage(amperage);
        setExpVoltage(voltage);
    }

    public double getExpResistance() {
        return expResistance.get();
    }

    public void setExpResistance(double expResistance) {
        try {
            this.expResistance.set(expResistance);
        } catch (NullPointerException nullExc) {
            this.expResistance.set(0);
        }
    }

    public SimpleDoubleProperty expResistanceProperty() {
        return expResistance;
    }

    public double getExpAB_2() {
        return expAB_2.get();
    }

    public void setExpAB_2(double expAB_2) {
        try {
            this.expAB_2.set(expAB_2);
        } catch (NullPointerException nullExc) {
            this.expAB_2.set(0);
        }
    }

    public SimpleDoubleProperty expAB_2Property() {
        return expAB_2;
    }

    public double getExpMN_2() {
        return expMN_2.get();
    }

    public void setExpMN_2(double expMN_2) {
        try {
            this.expMN_2.set(expMN_2);
        } catch (NullPointerException nullExc) {
            this.expMN_2.set(0);
        }
    }

    public SimpleDoubleProperty expMN_2Property() {
        return expMN_2;
    }

    public double getExpErrorResistance() {
        return expErrorResistance.get();
    }

    public void setExpErrorResistance(double expErrorResistance) {
        try {
            this.expErrorResistance.set(expErrorResistance);
        } catch (NullPointerException nullExc) {
            this.expErrorResistance.set(0);
        }
    }

    public SimpleDoubleProperty expErrorResistanceProperty() {
        return expErrorResistance;
    }

    public double getExpPolarisation() {
        return expPolarisation.get();
    }

    public void setExpPolarisation(double expPolarisation) {
        try {
            this.expPolarisation.set(expPolarisation);
        } catch (NullPointerException nullExc) {
            this.expPolarisation.set(0);
        }
    }

    public SimpleDoubleProperty expPolarisationProperty() {
        return expPolarisation;
    }

    public double getExpErrorPolarisation() {
        return expErrorPolarisation.get();
    }

    public void setExpErrorPolarisation(double expErrorPolarisation) {
        try {
            this.expErrorPolarisation.set(expErrorPolarisation);
        } catch (NullPointerException nullExc) {
            this.expErrorPolarisation.set(0);
        }
    }

    public SimpleDoubleProperty expErrorPolarisationProperty() {
        return expErrorPolarisation;
    }

    public double getExpAmperage() {
        return expAmperage.get();
    }

    public void setExpAmperage(double expAmperage) {
        try {
            this.expAmperage.set(expAmperage);
        } catch (NullPointerException nullExc) {
            this.expAmperage.set(0);
        }
    }

    public SimpleDoubleProperty expAmperageProperty() {
        return expAmperage;
    }

    public double getExpVoltage() {
        return expVoltage.get();
    }

    public void setExpVoltage(double expVoltage) {
        try {
            this.expVoltage.set(0);
        } catch (NullPointerException nullExc) {
            this.expVoltage.set(0);
        }
    }

    public SimpleDoubleProperty expVoltageProperty() {
        return expVoltage;
    }
}
