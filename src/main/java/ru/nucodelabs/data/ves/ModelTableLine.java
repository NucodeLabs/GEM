package ru.nucodelabs.data.ves;

public class ModelTableLine {

    private int index;
    private double resistance;
    private double power;
    private double polarization;

    public ModelTableLine(
            int index,
            Double resistance,
            Double power,
            Double polarization) {
        this.index = index;
        this.power = power;
        this.resistance = resistance;
        this.polarization = polarization;
    }

    public int getIndex() {
        return index;
    }

    public double getResistance() {
        return resistance;
    }

    public double getPower() {
        return power;
    }

    public double getPolarization() {
        return polarization;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setResistance(double resistance) {
        this.resistance = resistance;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public void setPolarization(double polarization) {
        this.polarization = polarization;
    }
}
