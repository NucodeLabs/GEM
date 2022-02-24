package ru.nucodelabs.gem.model;

/**
 * Data model which represents configuration/settings of program
 */
public interface Config extends Model {

    /**
     * Factory method
     *
     * @return config model instance
     */
    static Config create() {
        return new ConfigImpl();
    }

    //TODO: config interface
}
