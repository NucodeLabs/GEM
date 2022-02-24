package ru.nucodelabs.gem.core;

import ru.nucodelabs.gem.model.Config;
import ru.nucodelabs.gem.model.Section;

/**
 * Gives references to models
 */
public class ModelProvider {

    private final Config config; // singleton

    public ModelProvider() {
        config = Config.create();
    }

    public Section getSection() {
        return Section.create();
    }

    public Config getConfig() {
        return config;
    }
}
