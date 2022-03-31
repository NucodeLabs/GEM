package ru.nucodelabs.gem.app.command;

import ru.nucodelabs.data.ves.Picket;

import java.util.List;

public abstract class AbstractCommand implements Command {

    protected final List<Picket> state;
    private final List<Picket> backup;

    public AbstractCommand(List<Picket> state) {
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
