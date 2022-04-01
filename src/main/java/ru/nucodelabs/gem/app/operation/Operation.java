package ru.nucodelabs.gem.app.operation;

public interface Operation {
    void restoreSubjectFromSnapshot();

    boolean execute();
}
