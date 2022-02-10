package ru.nucodelabs.gem.core;

import ru.nucodelabs.gem.model.ConfigManager;
import ru.nucodelabs.gem.model.ConfigModel;
import ru.nucodelabs.gem.model.VESDataManager;
import ru.nucodelabs.gem.model.VESDataModel;

/**
 * <h2>Model Factory</h2>
 * Gives references to models
 */
public class ModelFactory {

    private final ConfigModel configModel; // singleton

    public ModelFactory() {
        configModel = new ConfigManager();
    }

    public VESDataModel createVESDataModel() {
        return new VESDataManager();
    }

    public ConfigModel getConfigModel() {
        return configModel;
    }
}
