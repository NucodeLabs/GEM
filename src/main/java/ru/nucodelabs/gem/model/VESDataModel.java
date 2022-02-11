package ru.nucodelabs.gem.model;

import ru.nucodelabs.data.ves.ExperimentalData;
import ru.nucodelabs.data.ves.ModelData;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.mvvm.Model;

import java.util.List;

/**
 * Data model which basically represents section, an ordered set of pickets
 */
public interface VESDataModel extends Model {
    /**
     * Returns model data of picket
     *
     * @param picketNumber index of picket in list of pickets
     * @return model data
     */
    ModelData getModelData(int picketNumber);

    /**
     * Sets model data of picket
     *
     * @param picketNumber index of picket in list of pickets
     * @param modelData    model data to set
     */
    void setModelData(int picketNumber, ModelData modelData);

    /**
     * Returns experimental data of picket
     *
     * @param picketNumber index of picket in list of pickets
     * @return experimental data
     */
    ExperimentalData getExperimentalData(int picketNumber);

    /**
     * Sets experimental data of picket
     *
     * @param picketNumber     index of picket in list of pickets
     * @param experimentalData experimental data to set
     */
    void setExperimentalData(int picketNumber, ExperimentalData experimentalData);

    /**
     * Returns list of pickets
     *
     * @return list of pickets
     */
    List<Picket> getPickets();

    /**
     * Returns picket
     *
     * @param picketNumber index of picket in list of pickets
     * @return picket
     */
    Picket getPicket(int picketNumber);

    /**
     * Adds picket to list of pickets
     *
     * @param picket picket to add
     */
    void addPicket(Picket picket);

    /**
     * Removes picket from list of pickets
     *
     * @param picketNumber index of picket in list of pickets
     */
    void removePicket(int picketNumber);

    /**
     * Returns size of pickets list
     *
     * @return count
     */
    int getPicketsCount();

    /**
     * Returns last picket in pickets list
     *
     * @return picket
     */
    Picket getLastPicket();
}
