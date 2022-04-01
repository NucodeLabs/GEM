package ru.nucodelabs.gem.app.operation;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class OperationExecutor {

    private List<Operation> history = new ArrayList<>();
    private int positionInHistory = 0;

    @Inject
    public OperationExecutor() {
    }

    public void execute(Operation operation) {
        if (operation.execute()) {
            push(operation);
        }
    }

    public void redo() {
        getRedo().ifPresent(Operation::execute);
    }

    private void push(Operation operation) {
        if (positionInHistory < history.size()) {
            history = history.subList(0, max(0, positionInHistory));
        }
        history.add(operation);
        positionInHistory = history.size();
    }

    public void undo() {
        getUndo().ifPresent(Operation::undo);
    }

    private Optional<Operation> getUndo() {
        if (positionInHistory == 0) {
            return Optional.empty();
        }
        positionInHistory = max(0, positionInHistory - 1);
        return Optional.of(history.get(positionInHistory));
    }

    private Optional<Operation> getRedo() {
        if (positionInHistory == history.size()) {
            return Optional.empty();
        }
        positionInHistory = min(history.size(), positionInHistory + 1);
        return Optional.of(history.get(positionInHistory - 1));
    }

    public void clearHistory() {
        history.clear();
        positionInHistory = 0;
    }
}
