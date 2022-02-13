package ru.nucodelabs.gem.view.usercontrols.vestables.tablelines;

import javafx.beans.property.SimpleDoubleProperty;

public class ModelTableLine {
    /**
     * Сопротивление слоя модели
     */
    private final SimpleDoubleProperty resistance;
    /**
     * Толщина слоя модели
     */
    private final SimpleDoubleProperty power;
    /**
     * Поляризация слоя модели
     */
    private final SimpleDoubleProperty polarization;

    public ModelTableLine(Double resistance,
                          Double power,
                          Double polarization) {
        this.resistance = new SimpleDoubleProperty(resistance);
        this.power = new SimpleDoubleProperty(power);
        this.polarization = new SimpleDoubleProperty(polarization);
    }

    public double getResistance() {
        return resistance.get();
    }

    public SimpleDoubleProperty resistanceProperty() {
        return resistance;
    }

    public void setResistance(double resistance) {
        this.resistance.set(resistance);
    }

    public double getPower() {
        return power.get();
    }

    public SimpleDoubleProperty powerProperty() {
        return power;
    }

    public void setPower(double power) {
        this.power.set(power);
    }

    public double getPolarization() {
        return polarization.get();
    }

    public SimpleDoubleProperty polarizationProperty() {
        return polarization;
    }

    public void setPolarization(double polarization) {
        this.polarization.set(polarization);
    }
}
