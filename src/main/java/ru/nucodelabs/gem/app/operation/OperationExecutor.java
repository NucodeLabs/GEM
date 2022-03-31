package ru.nucodelabs.gem.app.operation;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class OperationExecutor {

    private List<Operation> history = new ArrayList<>();
    private int virtualSize = 0;

    @Inject
    public OperationExecutor() {
    }

    public void execute(Operation operation) {
        if (operation.execute()) {
            push(operation);
        }
    }

    public void redo() {
        Operation operation = getRedo();
        if (operation != null) {
            operation.execute();
        }
    }

    private void push(Operation operation) {
        if (virtualSize != history.size() && virtualSize > 0) {
            history = history.subList(0, virtualSize - 1);
        }
        history.add(operation);
        virtualSize = history.size();
    }

    public void undo() {
        Operation operation = getUndo();
        if (operation != null) {
            operation.undo();
        }
    }

    private Operation getUndo() {
        if (virtualSize == 0) {
            return null;
        }
        virtualSize = Math.max(0, virtualSize - 1);
        return history.get(virtualSize);
    }

    private Operation getRedo() {
        if (virtualSize == history.size()) {
            return null;
        }
        virtualSize = Math.min(history.size(), virtualSize + 1);
        return history.get(virtualSize - 1);
    }

    public void clearHistory() {
        history.clear();
        virtualSize = 0;
    }
}
