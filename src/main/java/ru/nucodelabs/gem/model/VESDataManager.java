package ru.nucodelabs.gem.model;

import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;

import java.util.ArrayList;
import java.util.List;

public class VESDataManager implements VESDataModel {
    private final List<Picket> pickets;

    public VESDataManager() {
        pickets = new ArrayList<>(1);
    }

    @Override
    public ModelData getModelData(int picketNumber) {
        return pickets.get(picketNumber).getModelData();
    }

    @Override
    public void setModelData(int picketNumber, ModelData modelData) {
        pickets.get(picketNumber).setModelData(modelData);
    }

    @Override
    public ExperimentalData getExperimentalData(int picketNumber) {
        return pickets.get(picketNumber).getExperimentalData();
    }

    @Override
    public void setExperimentalData(int picketNumber, ExperimentalData experimentalData) {
        pickets.get(picketNumber).setExperimentalData(experimentalData);
    }

    @Override
    public List<Picket> getPickets() {
        return pickets;
    }

    @Override
    public Picket getPicket(int picketNumber) {
        return pickets.get(picketNumber);
    }

    @Override
    public void addPicket(Picket picket) {
        pickets.add(picket);
    }

    @Override
    public void removePicket(int picketNumber) {
        pickets.remove(picketNumber);
    }

    @Override
    public int getPicketsCount() {
        return pickets.size();
    }
}
