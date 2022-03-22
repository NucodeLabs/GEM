package ru.nucodelabs.gem.dao;

import javax.naming.OperationNotSupportedException;
import java.io.File;

public class ConfigImpl implements Config {
    @Override
    public void loadFromJson(File file) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    @Override
    public void saveToJson(File file) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }
}
