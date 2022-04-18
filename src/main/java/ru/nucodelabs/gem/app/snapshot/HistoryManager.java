package ru.nucodelabs.gem.app.snapshot;


import ru.nucodelabs.data.ves.Section;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class HistoryManager {

    private final Snapshot.Originator<Section> originator;
    private List<Snapshot<Section>> history = new ArrayList<>();
    private int position = 0;

    @Inject
    public HistoryManager(Snapshot.Originator<Section> originator) {
        this.originator = originator;
    }

    public void snapshot() {
        if (position < history.size() - 1) {
            history = history.subList(0, max(0, position + 1));
        }
        history.add(originator.getSnapshot());
        position = history.size() - 1;
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

    private Optional<Snapshot<Section>> getUndo() {
        if (position == 0 || history.isEmpty()) {
            return Optional.empty();
        }
        position = max(0, position - 1);
        return Optional.of(history.get(position));
    }

    private Optional<Snapshot<Section>> getRedo() {
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
