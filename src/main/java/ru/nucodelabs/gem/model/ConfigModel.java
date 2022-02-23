package ru.nucodelabs.gem.model;

import java.io.File;

/**
 * Data model which represents configuration/settings of program
 */
public interface ConfigModel extends Model {
    File getConfigFile();

    // to be continued
}
