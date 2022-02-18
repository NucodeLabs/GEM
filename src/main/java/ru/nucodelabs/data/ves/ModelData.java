package ru.nucodelabs.data.ves;

import ru.nucodelabs.files.sonet.MODFile;

import java.util.ArrayList;
import java.util.Collections;
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

    public ModelData() {
        resistance = new ArrayList<>();
        polarization = new ArrayList<>();
        power = new ArrayList<>();
    }

    public ModelData(MODFile modFile) {
        this();
        resistance = modFile.getResistance();
        polarization = modFile.getPolarization();
        power = modFile.getPower();
    }

    private List<Integer> sizesList() {
        List<Integer> sizes = new ArrayList<>();
        if (resistance.size() > 0) sizes.add(resistance.size());
        if (polarization.size() > 0) sizes.add(polarization.size());
        if (power.size() > 0) sizes.add(power.size());

        return sizes;
    }

    public int getSize() {
        return Collections.min(sizesList());
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
}
