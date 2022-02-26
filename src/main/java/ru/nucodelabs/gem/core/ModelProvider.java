package ru.nucodelabs.gem.core;

import ru.nucodelabs.gem.model.Config;
import ru.nucodelabs.gem.model.ConfigImpl;
import ru.nucodelabs.gem.model.Section;
import ru.nucodelabs.gem.model.SectionImpl;

/**
 * Gives references to models
 */
public class ModelProvider {

    private final Config config;

    public ModelProvider() {
        config = new ConfigImpl();
    }

    public Section getSection() {
        return new SectionImpl();
    }

    public Config getConfig() {
        return config;
    }
}
