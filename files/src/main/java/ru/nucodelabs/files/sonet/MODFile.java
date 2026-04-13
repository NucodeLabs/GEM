package ru.nucodelabs.files.sonet;

import java.io.File;
import java.util.ArrayList;

public class MODFile {
    private File file;

    private ArrayList<Double> resistivity; // Сопротивление, Ом*м
    private ArrayList<Double> polarization; // Поляризация, %
    private ArrayList<Double> power; // Мощность, м

    public MODFile() {
        resistivity = new ArrayList<>(0);
        polarization = new ArrayList<>(0);
        power = new ArrayList<>(0);
    }

    public int getColumnCnt() {
        return 3;
    }

    public ArrayList<Double> getResistivity() {
        return resistivity;
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

    public void setResistivity(ArrayList<Double> resistivity) {
        this.resistivity = resistivity;
    }

    public void setPower(ArrayList<Double> power) {
        this.power = power;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
