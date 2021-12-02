package ru.nucodelabs.data;

import ru.nucodelabs.files.sonet.MODFile;

import java.util.ArrayList;
import java.util.List;

public class ModelData {
    private List<Double> resistance; // Сопротивление, Ом*м
    private List<Double> polarization; // Поляризация, %
    private List<Double> power; // Мощность, м

    private List<Double> solvedResistance;

    public ModelData() {
        resistance = new ArrayList<>();
        polarization = new ArrayList<>();
        power = new ArrayList<>();
        solvedResistance = new ArrayList<>();
    }

    public ModelData(MODFile modFile) {
        this();
        resistance = modFile.getResistance();
        polarization = modFile.getPolarization();
        power = modFile.getPower();
    }

    public List<Double> getResistance() {
        return resistance;
    }

    public void setResistance(List<Double> resistance) {
        this.resistance = resistance;
    }

    public List<Double> getPolarization() {
        return polarization;
    }

    public void setPolarization(List<Double> polarization) {
        this.polarization = polarization;
    }

    public List<Double> getPower() {
        return power;
    }

    public void setPower(List<Double> power) {
        this.power = power;
    }

    public List<Double> getSolvedResistance() {
        return solvedResistance;
    }

    public void setSolvedResistance(List<Double> solvedResistance) {
        this.solvedResistance = solvedResistance;
    }
}
