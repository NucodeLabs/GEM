package ru.nucodelabs.data.ves;

import ru.nucodelabs.files.sonet.MODFile;

import java.util.ArrayList;
import java.util.List;

public class ModelData {
    /**
     * Сопротивление, Ом * м
     */
    private List<Double> resistance; // get set
    /**
     * Поляризация, %
     */
    private List<Double> polarization; // get set
    /**
     * Мощность, м
     */
    private List<Double> power; // get set

    private Integer size;

    public ModelData() {
        resistance = new ArrayList<>();
        polarization = new ArrayList<>();
        power = new ArrayList<>();
        size = 0;
    }

    public ModelData(MODFile modFile) {
        this();
        resistance = modFile.getResistance();
        polarization = modFile.getPolarization();
        power = modFile.getPower();
        size = resistance.size();
    }

    //region getters and setters
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
    //endregion

    public Integer getSize() {
        return size;
    }
}
