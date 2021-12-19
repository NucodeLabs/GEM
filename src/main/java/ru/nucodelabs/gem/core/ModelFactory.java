package ru.nucodelabs.gem.core;

import ru.nucodelabs.gem.model.VESDataManager;
import ru.nucodelabs.gem.model.VESDataModel;

/**
 * <h2>Model Factory</h2>
 * Gives references to models
 */
public class ModelFactory {

    private final VESDataModel vesDataModel;

    public ModelFactory() {
        vesDataModel = new VESDataManager();
    }

    public VESDataModel getVesDataModel() {
        return vesDataModel;
    }
}
