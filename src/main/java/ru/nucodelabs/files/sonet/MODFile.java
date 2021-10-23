package ru.nucodelabs.files.sonet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MODFile {
    public MODFile() {
    }

    public MODFile(File file) throws FileNotFoundException {
        Sonet.readMOD(file);
    }

    private ArrayList<Double> resistance = new ArrayList<>(0); // Сопротивление, Ом*м
    private ArrayList<Double> polarization = new ArrayList<>(0); // Поляризация, %
    private ArrayList<Double> power = new ArrayList<>(0); // Мощность, м

    public ArrayList<Double> getResistance() {
        return resistance;
    }

    public ArrayList<Double> getPolarization() {
        return polarization;
    }

    public ArrayList<Double> getPower() {
        return power;
    }

    public void setPolarization(ArrayList<Double> polarization) {
        this.polarization = polarization;
    }

    public void setResistance(ArrayList<Double> resistance) {
        this.resistance = resistance;
    }

    public void setPower(ArrayList<Double> power) {
        this.power = power;
    }
}
