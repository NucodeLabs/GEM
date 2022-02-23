package ru.nucodelabs.gem.model;

import javax.naming.OperationNotSupportedException;
import java.io.File;

public class ConfigManager implements ConfigModel {
    @Override
    public File getConfigFile() {
        return null;
    }

    @Override
    public void loadFromJson(File file) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    @Override
    public void saveToJson(File file) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }
}
