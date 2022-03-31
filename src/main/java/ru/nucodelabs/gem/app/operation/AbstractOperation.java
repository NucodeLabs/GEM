package ru.nucodelabs.gem.app.operation;

import ru.nucodelabs.data.ves.Picket;

import java.util.List;

public abstract class AbstractOperation implements Operation {

    protected final List<Picket> state;
    private final List<Picket> backup;

    public AbstractOperation(List<Picket> state) {
        this.state = state;
        backup = List.copyOf(state);
    }

    @Override
    public void undo() {
        state.clear();
        state.addAll(backup);
    }

    @Override
    public abstract boolean execute();
}
