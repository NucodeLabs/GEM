package ru.nucodelabs.gem.app.operation;

public interface Operation {
    void undo();

    boolean execute();
}
