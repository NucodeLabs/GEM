package ru.nucodelabs.data;

import java.util.ArrayList;

public class ExperimetalData {
    private ArrayList<Double> AB_2 = new ArrayList<>(0); // AB/2, м
    private ArrayList<Double> MN_2 = new ArrayList<>(0); // MN/2, м

    private ArrayList<Double> amperage = new ArrayList<>(0); // Ток, мА
    private ArrayList<Double> voltage = new ArrayList<>(0); // Напряжение, мВ
    private ArrayList<Double> resistanceApparent = new ArrayList<>(0); // Сопротивление кажущееся, Ом * м
    private ArrayList<Double> errorResistanceApparent = new ArrayList<>(0); // Погрешность, %
    private ArrayList<Double> polarizationApparent = new ArrayList<>(0); // Поляризация кажущаяся, %
    private ArrayList<Double> errorPolarizationApparent = new ArrayList<>(0); // Погрешность, %

    public ArrayList<Double> getAB_2() {
        return AB_2;
    }

    public void setAB_2(ArrayList<Double> AB_2) {
        this.AB_2 = AB_2;
    }

    public ArrayList<Double> getMN_2() {
        return MN_2;
    }

    public void setMN_2(ArrayList<Double> MN_2) {
        this.MN_2 = MN_2;
    }

    public ArrayList<Double> getAmperage() {
        return amperage;
    }

    public void setAmperage(ArrayList<Double> amperage) {
        this.amperage = amperage;
    }

    public ArrayList<Double> getVoltage() {
        return voltage;
    }

    public void setVoltage(ArrayList<Double> voltage) {
        this.voltage = voltage;
    }

    public ArrayList<Double> getResistanceApparent() {
        return resistanceApparent;
    }

    public void setResistanceApparent(ArrayList<Double> resistanceApparent) {
        this.resistanceApparent = resistanceApparent;
    }

    public ArrayList<Double> getErrorResistanceApparent() {
        return errorResistanceApparent;
    }

    public void setErrorResistanceApparent(ArrayList<Double> errorResistanceApparent) {
        this.errorResistanceApparent = errorResistanceApparent;
    }

    public ArrayList<Double> getPolarizationApparent() {
        return polarizationApparent;
    }

    public void setPolarizationApparent(ArrayList<Double> polarizationApparent) {
        this.polarizationApparent = polarizationApparent;
    }

    public ArrayList<Double> getErrorPolarizationApparent() {
        return errorPolarizationApparent;
    }

    public void setErrorPolarizationApparent(ArrayList<Double> errorPolarizationApparent) {
        this.errorPolarizationApparent = errorPolarizationApparent;
    }
}
