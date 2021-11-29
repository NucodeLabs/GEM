package ru.nucodelabs.data;

import java.util.ArrayList;

public class ModelData {
    private ArrayList<Double> resistance = new ArrayList<>(0); // Сопротивление, Ом*м
    private ArrayList<Double> polarization = new ArrayList<>(0); // Поляризация, %
    private ArrayList<Double> power = new ArrayList<>(0); // Мощность, м

    public ArrayList<Double> getResistance() {
        return resistance;
    }

    public void setResistance(ArrayList<Double> resistance) {
        this.resistance = resistance;
    }

    public ArrayList<Double> getPolarization() {
        return polarization;
    }

    public void setPolarization(ArrayList<Double> polarization) {
        this.polarization = polarization;
    }

    public ArrayList<Double> getPower() {
        return power;
    }

    public void setPower(ArrayList<Double> power) {
        this.power = power;
    }
}
