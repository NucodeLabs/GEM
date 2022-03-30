package ru.nucodelabs.gem.app.command;

public interface Command {
    void undo();

    boolean execute();
}
