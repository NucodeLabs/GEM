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

    public ModelData(MODFile modFile) {
        resistance = modFile.getResistance();
        polarization = modFile.getPolarization();
        power = modFile.getPower();
    }

    private List<Integer> sizesList() {
        List<Integer> sizes = new ArrayList<>();
        if (resistance != null && resistance.size() > 0) sizes.add(resistance.size());
        if (polarization != null && polarization.size() > 0) sizes.add(polarization.size());
        if (power != null && power.size() > 0) sizes.add(power.size());

        return sizes;
    }

    public int getSize() {
        if (sizesList().size() > 0) {
            return Collections.min(sizesList());
        } else {
            return 0;
        }
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
