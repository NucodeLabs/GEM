package ru.nucodelabs.gem.app.command;

import ru.nucodelabs.data.ves.Picket;

import java.util.List;

public abstract class AbstractSectionCommand implements Command {

    private final List<Picket> pickets;
    private List<Picket> backup;

    public AbstractSectionCommand(List<Picket> currentState) {
        this.pickets = currentState;
        backup();
    }

    @Override
    public void undo() {
        pickets.clear();
        pickets.addAll(backup);
    }

    @Override
    public abstract boolean execute();

    private void backup() {
        backup = List.copyOf(pickets);
    }
}
