package ru.nucodelabs.gem.core;

import ru.nucodelabs.gem.model.ConfigManager;
import ru.nucodelabs.gem.model.ConfigModel;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.model.SectionImpl;

/**
 * Gives references to models
 */
public class ModelFactory {

    private final ConfigModel configModel; // singleton

    public ModelFactory() {
        configModel = new ConfigManager();
    }

    public Section createSection() {
        return new SectionImpl();
    }

    public ConfigModel getConfigModel() {
        return configModel;
    }
}
