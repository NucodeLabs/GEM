package ru.nucodelabs.gem.model;

import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.mvvm.Model;

import java.util.List;

public interface VESDataModel extends Model {
    ModelData getModelData(int picketNumber);

    void setModelData(int picketNumber, ModelData modelData);

    ExperimentalData getExperimentalData(int picketNumber);

    void setExperimentalData(int picketNumber, ExperimentalData experimentalData);

    List<Picket> getPickets();

    Picket getPicket(int picketNumber);

    void addPicket(Picket picket);

    void removePicket(int picketNumber);

    int getPicketsCount();
}
