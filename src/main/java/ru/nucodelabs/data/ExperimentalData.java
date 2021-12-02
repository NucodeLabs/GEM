package ru.nucodelabs.data;

import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.STTFile;

import java.util.ArrayList;
import java.util.List;

public class ExperimentalData {
    private List<Double> AB_2; // AB/2, м
    private List<Double> MN_2; // MN/2, м

    private List<Double> amperage; // Ток, мА
    private List<Double> voltage; // Напряжение, мВ
    private List<Double> resistanceApparent; // Сопротивление кажущееся, Ом * м
    private List<Double> errorResistanceApparent; // Погрешность, %
    private List<Double> polarizationApparent; // Поляризация кажущаяся, %
    private List<Double> errorPolarizationApparent; // Погрешность, %

    public ExperimentalData() {
        AB_2 = new ArrayList<>();
        MN_2 = new ArrayList<>();
        amperage = new ArrayList<>();
        voltage = new ArrayList<>();
        resistanceApparent = new ArrayList<>();
        errorResistanceApparent = new ArrayList<>();
        polarizationApparent = new ArrayList<>();
        errorPolarizationApparent = new ArrayList<>();
    }

    public ExperimentalData(EXPFile expFile, STTFile sttFile) {
        AB_2 = sttFile.getAB_2();
        MN_2 = sttFile.getMN_2();
        amperage = expFile.getAmperage();
        voltage = expFile.getVoltage();
        resistanceApparent = expFile.getResistanceApparent();
        errorResistanceApparent = expFile.getErrorResistanceApparent();
        polarizationApparent = expFile.getPolarizationApparent();
        errorPolarizationApparent = expFile.getErrorPolarizationApparent();
    }

    public List<Double> getAB_2() {
        return AB_2;
    }

    public void setAB_2(List<Double> AB_2) {
        this.AB_2 = AB_2;
    }

    public List<Double> getMN_2() {
        return MN_2;
    }

    public void setMN_2(List<Double> MN_2) {
        this.MN_2 = MN_2;
    }

    public List<Double> getAmperage() {
        return amperage;
    }

    public void setAmperage(List<Double> amperage) {
        this.amperage = amperage;
    }

    public List<Double> getVoltage() {
        return voltage;
    }

    public void setVoltage(List<Double> voltage) {
        this.voltage = voltage;
    }

    public List<Double> getResistanceApparent() {
        return resistanceApparent;
    }

    public void setResistanceApparent(List<Double> resistanceApparent) {
        this.resistanceApparent = resistanceApparent;
    }

    public List<Double> getErrorResistanceApparent() {
        return errorResistanceApparent;
    }

    public void setErrorResistanceApparent(List<Double> errorResistanceApparent) {
        this.errorResistanceApparent = errorResistanceApparent;
    }

    public List<Double> getPolarizationApparent() {
        return polarizationApparent;
    }

    public void setPolarizationApparent(List<Double> polarizationApparent) {
        this.polarizationApparent = polarizationApparent;
    }

    public List<Double> getErrorPolarizationApparent() {
        return errorPolarizationApparent;
    }

    public void setErrorPolarizationApparent(List<Double> errorPolarizationApparent) {
        this.errorPolarizationApparent = errorPolarizationApparent;
    }
}
