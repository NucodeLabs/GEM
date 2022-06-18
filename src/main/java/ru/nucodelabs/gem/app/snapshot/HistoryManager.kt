package ru.nucodelabs.gem.app.snapshot;


import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class HistoryManager<T> {

    private final Snapshot.Originator<T> originator;
    private List<Snapshot<T>> history = new ArrayList<>();
    private int position = 0;

    @Inject
    public HistoryManager(Snapshot.Originator<T> originator) {
        this.originator = originator;
    }

    public void snapshot() {
        Snapshot<T> snapshot = originator.getSnapshot();
        if (history.isEmpty() || !Objects.equals(history.get(position), snapshot)) {
            if (position < history.size() - 1) {
                history = history.subList(0, max(0, position + 1));
            }
            history.add(snapshot);
            position = history.size() - 1;
        }
    }

    public void performThenSnapshot(Runnable operation) {
        operation.run();
        snapshot();
    }

    public void redo() {
        getRedo().ifPresent(originator::restoreFromSnapshot);
    }


    public void undo() {
        getUndo().ifPresent(originator::restoreFromSnapshot);
    }

    private Optional<Snapshot<T>> getUndo() {
        if (position == 0 || history.isEmpty()) {
            return Optional.empty();
        }
        position = max(0, position - 1);
        return Optional.of(history.get(position));
    }

    private Optional<Snapshot<T>> getRedo() {
        if (position == history.size() - 1 || history.isEmpty()) {
            return Optional.empty();
        }
        position = min(history.size() - 1, position + 1);
        return Optional.of(history.get(position));
    }

    public void clear() {
        history.clear();
        position = 0;
    }
}
