package ru.nucodelabs.gem.core;

import ru.nucodelabs.gem.model.ConfigManager;
import ru.nucodelabs.gem.model.ConfigModel;
import ru.nucodelabs.gem.model.VESDataManager;
import ru.nucodelabs.gem.model.VESDataModel;

import java.util.Objects;

/**
 * <h2>Model Factory</h2>
 * Gives references to models
 */
public class ModelFactory {

    private VESDataModel vesDataModel;
    private ConfigModel configModel;

    public ModelFactory() {
    }

    public VESDataModel getVesDataModel() {
        return Objects.requireNonNullElse(vesDataModel, new VESDataManager());
    }

    public ConfigModel getConfigModel() {
        return Objects.requireNonNullElse(configModel, new ConfigManager());
    }
}
