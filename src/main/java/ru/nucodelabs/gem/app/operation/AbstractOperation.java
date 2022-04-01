package ru.nucodelabs.gem.app.operation;

import ru.nucodelabs.data.ves.Picket;

import java.util.List;

public abstract class AbstractOperation implements Operation {

    protected final List<Picket> subject;
    private final List<Picket> snapshot;

    public AbstractOperation(List<Picket> subject) {
        this.subject = subject;
        snapshot = List.copyOf(subject);
    }

    @Override
    public void restoreSubjectFromSnapshot() {
        subject.clear();
        subject.addAll(snapshot);
    }

    @Override
    public abstract boolean execute();
}
