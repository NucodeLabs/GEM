package ru.nucodelabs.data.ves;

import ru.nucodelabs.files.sonet.EXPFile;
import ru.nucodelabs.files.sonet.STTFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExperimentalData {
    /**
     * AB/2, м
     */
    private List<Double> AB_2; // get set
    /**
     * MN/2, м
     */
    private List<Double> MN_2; // get set


    /**
     * Ток, мА
     */
    private List<Double> amperage; // get set
    /**
     * Напряжение, мВ
     */
    private List<Double> voltage; // get set
    /**
     * Сопротивление кажущееся, Ом * м
     */
    private List<Double> resistanceApparent; // get set
    /**
     * Погрешность, %
     */
    private List<Double> errorResistanceApparent; // get set
    /**
     * Поляризация кажущаяся, %
     */
    private List<Double> polarizationApparent; // get set
    /**
     * Погрешность, %
     */
    private List<Double> errorPolarizationApparent; // get set

    public ExperimentalData(EXPFile expFile, STTFile sttFile) throws IndexOutOfBoundsException {
        AB_2 = sttFile.getAB_2();
        MN_2 = sttFile.getMN_2();
        amperage = expFile.getAmperage();
        voltage = expFile.getVoltage();
        resistanceApparent = expFile.getResistanceApparent();
        errorResistanceApparent = expFile.getErrorResistanceApparent();
        polarizationApparent = expFile.getPolarizationApparent();
        errorPolarizationApparent = expFile.getErrorPolarizationApparent();
    }

    private List<Integer> sizesList() {
        List<Integer> sizes = new ArrayList<>();
        if (AB_2.size() > 0) sizes.add(AB_2.size());
        if (MN_2.size() > 0) sizes.add(MN_2.size());
        if (amperage.size() > 0) sizes.add(amperage.size());
        if (voltage.size() > 0) sizes.add(voltage.size());
        if (resistanceApparent.size() > 0) sizes.add(resistanceApparent.size());
        if (errorResistanceApparent.size() > 0) sizes.add(errorPolarizationApparent.size());
        if (polarizationApparent.size() > 0) sizes.add(polarizationApparent.size());
        if (errorPolarizationApparent.size() > 0) sizes.add(errorPolarizationApparent.size());

        return sizes;
    }

    public boolean isUnsafe() {
        return sizesList().stream().distinct().count() != 1;
    }

    public Integer getSize() {
        return Collections.min(sizesList());
    }

    //region getters and setters

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
    //endregion
}
