package ru.nucodelabs.gem.app;

import ru.nucodelabs.data.ves.Section;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class HistoryManager {

    private final SectionManager sectionManager;
    private List<Section> history = new ArrayList<>();
    private int position = 0;

    @Inject
    public HistoryManager(SectionManager sectionManager) {
        this.sectionManager = sectionManager;
    }

    public void snapshot() {
        if (position < history.size() - 1) {
            history = history.subList(0, max(0, position + 1));
        }
        history.add(sectionManager.getSnapshot());
        position = history.size() - 1;
    }

    public void performThenSnapshot(Runnable operation) {
        operation.run();
        snapshot();
    }

    public void redo() {
        getRedo().ifPresent(sectionManager::setSection);
    }


    public void undo() {
        getUndo().ifPresent(sectionManager::setSection);
    }

    private Optional<Section> getUndo() {
        if (position == 0) {
            return Optional.empty();
        }
        position = max(0, position - 1);
        return Optional.of(history.get(position));
    }

    private Optional<Section> getRedo() {
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
