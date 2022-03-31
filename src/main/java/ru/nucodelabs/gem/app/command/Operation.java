package ru.nucodelabs.gem.app.command;

public interface Operation {
    void undo();

    boolean execute();
}
