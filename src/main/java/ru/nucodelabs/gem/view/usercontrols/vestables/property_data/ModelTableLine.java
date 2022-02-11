package ru.nucodelabs.gem.view.usercontrols.vestables.property_data;

import javafx.beans.property.SimpleDoubleProperty;

public class ModelTableLine extends VESPropertyData {

    public ModelTableLine(Double resistance,
                          Double power,
                          Double polarisation) {
        setModResistance(resistance);
        setModPower(power);
        setModPolarisation(polarisation);
    }

    /** Сопротивление слоя модели
     */
    SimpleDoubleProperty modResistance;

    public double getModResistance() {
        return modResistance.get();
    }

    public SimpleDoubleProperty modResistanceProperty() {
        return modResistance;
    }

    public void setModResistance(double modResistance) {
        this.modResistance.set(modResistance);
    }

    /** Толщина слоя модели
     */
    SimpleDoubleProperty modPower;

    public double getModPower() {
        return modPower.get();
    }

    public SimpleDoubleProperty modPowerProperty() {
        return modPower;
    }

    public void setModPower(double modPower) {
        this.modPower.set(modPower);
    }

    /** Поляризация слоя модели
     */
    SimpleDoubleProperty modPolarisation;

    public double getModPolarisation() {
        return modPolarisation.get();
    }

    public SimpleDoubleProperty modPolarisationProperty() {
        return modPolarisation;
    }

    public void setModPolarisation(double modPolarisation) {
        this.modPolarisation.set(modPolarisation);
    }
}
