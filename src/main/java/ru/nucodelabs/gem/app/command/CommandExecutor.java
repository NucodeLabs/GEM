package ru.nucodelabs.gem.app.command;

import com.google.inject.Injector;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class CommandExecutor {

    @Inject
    private Injector injector;
    private List<Command> history = new ArrayList<>();
    private int virtualSize = 0;

    @Inject
    public CommandExecutor() {
    }

    public void execute(Command command) {
        injector.injectMembers(command);
        if (command.execute()) {
            push(command);
        }
    }

    public void redo() {
        Command command = getRedo();
        if (command != null) {
            command.execute();
        }
    }

    private void push(Command command) {
        if (virtualSize != history.size() && virtualSize > 0) {
            history = history.subList(0, virtualSize - 1);
        }
        history.add(command);
        virtualSize = history.size();
    }

    public void undo() {
        Command command = getUndo();
        if (command != null) {
            command.undo();
        }
    }

    private Command getUndo() {
        if (virtualSize == 0) {
            return null;
        }
        virtualSize = Math.max(0, virtualSize - 1);
        return history.get(virtualSize);
    }

    private Command getRedo() {
        if (virtualSize == history.size()) {
            return null;
        }
        virtualSize = Math.min(history.size(), virtualSize + 1);
        return history.get(virtualSize - 1);
    }
}
