package ru.nucodelabs.gem.app.command;

import ru.nucodelabs.data.ves.Picket;

import java.util.List;

public abstract class AbstractCommand implements Command {

    protected final List<Picket> currentState;
    private final List<Picket> backup;

    public AbstractCommand(List<Picket> currentState) {
        this.currentState = currentState;
        backup = List.copyOf(currentState);
    }

    @Override
    public void undo() {
        currentState.clear();
        currentState.addAll(backup);
    }

    @Override
    public abstract boolean execute();
}
