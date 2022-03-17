package ru.nucodelabs.gem.dao;

import java.io.File;

/**
 * Abstract data Model interface that should be extended by different data model interfaces
 */
public interface Model {
    /**
     * Load data from JSON file
     *
     * @param file JSON file
     * @throws Exception if file is incorrect or IOException
     */
    void loadFromJson(File file) throws Exception;

    /**
     * Load data from JSON file
     *
     * @param file JSON file
     * @throws Exception IOException or OperationNotSupported
     */
    void saveToJson(File file) throws Exception;
}
